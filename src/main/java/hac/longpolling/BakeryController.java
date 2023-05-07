package hac.longpolling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;

/**
 * Long polling controller example. Source: https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-rest-http-2
 *
 * try with postman: http://localhost:8080/api/bake/cookie?bakeTime=5000
 */
@RestController
@RequestMapping("/api")
public class BakeryController {
    private final static Logger LOG = LoggerFactory.getLogger(BakeryController.class);
    private final static Long LONG_POLLING_TIMEOUT = 5000L;

    private ExecutorService bakers;

    public BakeryController() {
        // create a thread pool of 5 bakers
        bakers = Executors.newFixedThreadPool(5);
    }

    @GetMapping("/bake/{bakedGood}")
    public @ResponseBody DeferredResult<MyResponse> publisher(@PathVariable String bakedGood) {

        // create a deferred result that will be returned to the client
        DeferredResult<MyResponse> output = new DeferredResult<>(LONG_POLLING_TIMEOUT);
        output.onCompletion(() -> LOG.info("Request completed"));
        // set a timeout for the deferred result, in which case the client will be notified about the timeout
        output.onTimeout(() -> output.setErrorResult(new MyResponse("the bakery is not responding in allowed time")));
        // generate a random bake time between 1000 and 10000 ms
        int bakeTime = 1000 + (int) (Math.random() * 9000);
        //Integer finalBakeTime = bakeTime;
        bakers.execute(() -> {
            try {

                Thread.sleep(bakeTime);
                // set the result of the deferred result to the client
                output.setResult(new MyResponse(format("Bake for %s complete and order dispatched in %sms. Enjoy!", bakedGood, bakeTime)));
            } catch (Exception e) {
                output.setErrorResult(new MyResponse("Something went wrong with your order!"));
            }
        });


        return output;
    }
}

class MyResponse {
    private String message;

    public MyResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}