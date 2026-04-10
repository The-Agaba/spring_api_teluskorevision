package com.springboot.store.payment;

import com.springboot.store.entities.Order;
import com.springboot.store.entities.OrderItem;
import com.springboot.store.entities.PaymentStatus;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StripePaymentGateway implements PaymentGateway {
    @Value("${websiteUrl}")
    private String websiteUrl;
    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try{
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl+ "/checkout-success?orderId="+order.getId())
                    .setCancelUrl(websiteUrl+ "/checkout-cancel")
                    .putMetadata("order_id", order.getId().toString());

            order.getItems().forEach(item->{
                var lineItem = createLineItem(item);
                builder.addLineItem(lineItem);
            });

            var session= Session.create(builder.build());
            return new CheckoutSession(session.getUrl());
        } catch (StripeException e) {
            System.out.println(e.getMessage());
            throw new PaymentException();
        }

    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {


            var payload = request.getPayload();
//            var signature = request.getHeaders().get("stripe-signature");
            var signature = request.getHeaders().get("stripe-signature") != null
                    ? request.getHeaders().get("stripe-signature")
                    : request.getHeaders().get("Stripe-Signature");
            var event = Webhook.constructEvent(payload, signature, webhookSecretKey);
            System.out.println(event.getType());


            switch (event.getType()) {
                case "payment_intent.succeeded" -> {
                    return Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.PAID));
                }


                case "payment_intent.failed" -> {
                    return Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.FAILED));
                }

                default -> Optional.empty();

            }
        }catch (SignatureVerificationException e){
            throw new PaymentException("Invalid signature");
        } catch (EventDataObjectDeserializationException e) {
            throw new RuntimeException(e);
        }
        // FIX: added missing return statement — without this the method had no return path
        // after the switch block, causing a compilation error
        return Optional.empty();
    }


    private Long extractOrderId(Event event) throws EventDataObjectDeserializationException {
        // FIX: switched to deserializeUnsafe() to match your SDK version
        // The getObject() approach was commented out because it requires a newer SDK version
        var paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().deserializeUnsafe();

        // REMOVED: the old getObject() line that didn't match your SDK
        // var stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
        // var paymentIntent = (PaymentIntent) stripeObject;

        // REMOVED: the if-block below was dead code — orderId was assigned but never used or returned
        // if (paymentIntent != null) {
        //     var orderId = paymentIntent.getMetadata().get("order_id");
        // }

        // REMOVED: assert is unreliable in production (disabled by default by JVM)
        // assert paymentIntent != null;

        // FIX: proper null check before accessing metadata, throws a clear exception if null
        if (paymentIntent == null) {

            throw new PaymentException("PaymentIntent could not be deserialized");
        }
          //for testing
        System.out.println("Metadata received: " + paymentIntent.getMetadata());
        System.out.println("order_id value: " + paymentIntent.getMetadata().get("order_id"));

        var orderId = paymentIntent.getMetadata().get("order_id");
        if (orderId == null || orderId.isBlank()) {
            throw new PaymentException("Missing order_id in PaymentIntent metadata");
        }

        return Long.valueOf(paymentIntent.getMetadata().get("order_id"));
    }

    private static SessionCreateParams.LineItem createLineItem(OrderItem item) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(CreatePriceData(item))
                .build();
    }

    private static SessionCreateParams.LineItem.PriceData CreatePriceData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("USD")
                .setUnitAmountDecimal(
                        item.getUnitPrice() .multiply(BigDecimal.valueOf(100)))
                .setProductData(
                        createProductData(item)
                )
                .build();
    }

    private static SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName())
                .build();
    }

}