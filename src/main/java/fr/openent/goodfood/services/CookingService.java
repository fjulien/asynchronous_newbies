package fr.openent.goodfood.services;

import fr.wseduc.webutils.Either;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public interface CookingService {
    void makeFrenchFries(final String choice, final String order, final Handler<Either<String, JsonObject>> handler);

    void makeHamburger(final String choice, final String order, final Handler<Either<String, JsonObject>> handler);

    void makeDrink(final String choice, final String order, final Handler<Either<String, JsonObject>> handler);
}
