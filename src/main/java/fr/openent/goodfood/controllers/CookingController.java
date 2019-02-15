package fr.openent.goodfood.controllers;

import fr.openent.goodfood.services.CookingService;
import fr.wseduc.rs.Get;
import fr.wseduc.rs.Post;
import fr.wseduc.security.SecuredAction;
import fr.wseduc.webutils.Either;
import fr.wseduc.webutils.http.BaseController;
import fr.wseduc.webutils.request.RequestUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

public class CookingController extends BaseController {

    CookingService cookingService;

    public CookingController(CookingService cookingService) {
        this.cookingService = cookingService;
    }

    @Get("/securedAction")
    @SecuredAction("goodfood.openent.SecuredActionhack")
    public void securedAction(final HttpServerRequest request) {
    }

    @Post("/order/simple")
    public void simpleOrder(final HttpServerRequest request) {
        RequestUtils.bodyToJson(request, (JsonObject entries) -> {
            if (entries.containsKey("burger_choice")
                    && entries.containsKey("friesSausage_choice")
                    && entries.containsKey("drink_choice")) {
                String order_number = "1234b1";

                // call 1 - enter point
                cookingService.makeFrenchFries(entries.getString("burger_choice"), order_number, new Handler<Either<String, JsonObject>>() {
                    @Override
                    public void handle(Either<String, JsonObject> finalFries) {
                        if (finalFries.isRight()) {

                            // call 2
                            cookingService.makeHamburger(entries.getString("friesSausage_choice"), order_number, finalBurger -> {
                                if (finalBurger.isRight()) {

                                    // call 3
                                    cookingService.makeDrink(entries.getString("drink_choice"), order_number, finalDrink -> {
                                        // Final Response
                                        if (finalDrink.isRight()) {
                                            request.response()
                                                    .putHeader("Content-type", "text/html;charset=utf-8")
                                                    .setChunked(true)
                                                    .setStatusCode(201)
                                                    .end("order sended");
                                        } else {
                                            badRequest(request, "Drinking problem");
                                        }
                                    });
                                } else {
                                    badRequest(request, "Burger problem");
                                }
                            });
                        } else {
                            badRequest(request, "Fries problem");
                        }
                    }
                });
            } else {
                badRequest(request, "No valid params");
            }
        });
    }

    @Post("/order/organized")
    public void organizedOrder(final HttpServerRequest request) {
        RequestUtils.bodyToJson(request, (JsonObject entries) -> {
            if (entries.containsKey("burger_choice")
                    && entries.containsKey("friesSausage_choice")
                    && entries.containsKey("drink_choice")) {
                String order_number = "1234b1";

                // Final Response
                Handler<Either<String, JsonObject>> makeDrinkHandler = finalDrink -> {
                    if (finalDrink.isRight()) {
                        request.response()
                                .putHeader("Content-type", "text/html;charset=utf-8")
                                .setChunked(true)
                                .setStatusCode(201)
                                .end("order sended");
                    } else {
                        badRequest(request, "Drinking problem");
                    }
                };

                Handler<Either<String, JsonObject>> makeBurgerHandler = finalburger -> {
                    if (finalburger.isRight()) {
                        // call 3
                        cookingService.makeDrink(entries.getString("drink_choice"), order_number, makeDrinkHandler);
                    } else {
                        badRequest(request, "Burger problem");
                    }
                };

                Handler<Either<String, JsonObject>> makeFrenchFriesHandler = finalFries -> {
                    if (finalFries.isRight()) {
                        // call 2
                        cookingService.makeHamburger(entries.getString("burger_choice"), order_number, makeBurgerHandler);
                    } else {
                        badRequest(request, "Fries problem");
                    }
                };

                // call 1 - enter point
                cookingService.makeFrenchFries(entries.getString("friesSausage_choice"), order_number, makeFrenchFriesHandler);

            } else {
                badRequest(request, "No valid params");
            }
        });
    }

    @Post("/order/futured")
    public void futuredOrder(final HttpServerRequest request) {
        RequestUtils.bodyToJson(request, (JsonObject entries) -> {
            if (entries.containsKey("burger_choice")
                    && entries.containsKey("friesSausage_choice")
                    && entries.containsKey("drink_choice")) {
                String order_number = "1234b1";

                // Future / Handler 3
                Future<JsonObject> makeDrinkFuture = Future.future();
                Handler<Either<String, JsonObject>> makeDrinkHandler = finalDrink -> {
                    if (finalDrink.isRight()) {
                        makeDrinkFuture.complete(finalDrink.right().getValue());
                    } else {
                        makeDrinkFuture.fail( "Drinking problem");
                    }
                };

                //call 3
                cookingService.makeDrink(entries.getString("drink_choice"), order_number, makeDrinkHandler);

                // Future / Handler 2
                Future<JsonObject> makeBurgerFuture = Future.future();
                Handler<Either<String, JsonObject>> makeBurgerHandler = finalburger -> {
                    if (finalburger.isRight()) {
                        makeBurgerFuture.complete(finalburger.right().getValue());
                    } else {
                        makeBurgerFuture.fail("Burger problem");
                    }
                };

                // call 2
                cookingService.makeHamburger(entries.getString("burger_choice"), order_number, makeBurgerHandler);

                // Future / Handler 1
                Future<JsonObject> makeFrenchFriesFuture = Future.future();
                Handler<Either<String, JsonObject>> makeFrenchFriesHandler = finalFries -> {
                    if (finalFries.isRight()) {
                        makeFrenchFriesFuture.complete(finalFries.right().getValue());
                    } else {
                        makeFrenchFriesFuture.fail("Fries problem");
                    }
                };

                // call 1 - enter point
                cookingService.makeFrenchFries(entries.getString("friesSausage_choice"), order_number, makeFrenchFriesHandler);

                // Final Response
                CompositeFuture.all(makeBurgerFuture, makeFrenchFriesFuture, makeDrinkFuture).setHandler( event -> {
                    if (event.succeeded()) {
                        JsonObject burger = makeBurgerFuture.result(); //return object from service
                        JsonObject fries = makeFrenchFriesFuture.result(); //return object from service
                        JsonObject drink = makeDrinkFuture.result(); //return object from service
                        request.response()
                                .putHeader("Content-type", "text/html;charset=utf-8")
                                .setChunked(true)
                                .setStatusCode(201)
                                .end("order sended");
                    } else {
                        badRequest(request, event.cause().getMessage());
                    }
                });
            } else {
                badRequest(request, "No valid params");
            }
        });
    }
}


