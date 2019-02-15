package fr.openent.goodfood.services.impl;

import fr.openent.goodfood.services.CookingService;
import fr.wseduc.webutils.Either;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public class CookingServiceImpl implements CookingService {

    @Override
    public void makeFrenchFries(String choice, String order, Handler<Either<String, JsonObject>> handler) {
        JsonObject burger = new JsonObject();
        burger.put("fries", choice);
        burger.put("order", order);
        handler.handle(new Either.Right<>(burger));
    }

    @Override
    public void makeHamburger(String choice, String order, Handler<Either<String, JsonObject>> handler) {
        JsonObject burger = new JsonObject();
        burger.put("burger", choice);
        burger.put("order", order);
        handler.handle(new Either.Right<>(burger));
    }

    @Override
    public void makeDrink(String choice, String order, Handler<Either<String, JsonObject>> handler) {
        JsonObject burger = new JsonObject();
        burger.put("drink", choice);
        burger.put("order", order);
        handler.handle(new Either.Right<>(burger));
    }
}
