package fr.openent.goodfood;

import fr.openent.goodfood.controllers.CookingController;
import fr.openent.goodfood.services.CookingService;
import fr.openent.goodfood.services.impl.CookingServiceImpl;
import org.entcore.common.http.BaseServer;


public class GoodFood extends BaseServer {

    @Override
    public void start() throws Exception {
        super.start();

        final CookingService cookingService = new CookingServiceImpl();

        addController(new CookingController(cookingService));
    }

}
