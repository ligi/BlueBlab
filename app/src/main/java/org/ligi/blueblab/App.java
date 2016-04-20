package org.ligi.blueblab;

import android.app.Application;
import android.support.v4.content.ContextCompat;
import com.chibatching.kotpref.Kotpref;
import java.util.UUID;
import org.ligi.blueblab.model.UserModel;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Kotpref.INSTANCE.init(this);

        userModel = new UserModel();

        if (!userModel.isInitialized()) {
            userModel.setColor(ContextCompat.getColor(this, R.color.faceColor1));
            userModel.setId(UUID.randomUUID().toString());
        }
    }

    public static UserModel userModel;

}
