package org.ligi.blueblab.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.ligi.axt.AXT;
import org.ligi.blueblab.App;
import org.ligi.blueblab.R;
import org.ligi.blueblab.model.User;

public class FindPeerActivity extends AppCompatActivity {

    public static final int SIZE = 5;
    public static final int CENTER = SIZE/2;

    @Bind(R.id.gridLayout)
    SquareGridByWidthLayout gridLayout;

    private AvatarController myAvatarController;

    private AvatarController[][] avatarControllers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.find_peers);

        ButterKnife.bind(this);

        final LayoutInflater from = LayoutInflater.from(this);

        avatarControllers = new AvatarController[SIZE][SIZE];

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                final View inflate = from.inflate(R.layout.avatar, gridLayout, false);

                gridLayout.addView(inflate);
                final AvatarController avatarView = new AvatarController(inflate);

                avatarControllers[x][y] = avatarView;

                if (x == CENTER && y == CENTER) {
                    myAvatarController = avatarView;
                    avatarView.setFaceColor(App.userModel.getColor());
                    avatarView.setMood(App.userModel.getMood());
                } else {
                    avatarView.rootView.setVisibility(View.INVISIBLE);
                }

                inflate.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(final View v, final MotionEvent event) {
                        AXT.at(FindPeerActivity.this).startCommonIntent().activityFromClass(ChatActivity.class);
                        return false;
                    }
                });
            }
        }

        myAvatarController.setName(App.userModel.getName());

        final Handler h = new Handler();

        h.post(new Runnable() {
            @Override
            public void run() {

                for (int x = 0; x < SIZE; x++) {
                    for (int y = 0; y < SIZE; y++) {

                        final AvatarController avatarController = avatarControllers[x][y];

                        if (avatarController != myAvatarController) {
                            avatarController.rootView.setVisibility(View.INVISIBLE);
                        }
                    }
                }

                userPositions.clear();
                userObjects.clear();

                for (final User user : App.transporter.getVisibleUsers()) {
                    if (!userPositions.containsKey(user.getId())) {
                        userPositions.put(user.getId(), getRandomPoint());
                        userObjects.put(user.getId(),user);
                    }
                }

                for (final Point point : userPositions.values()) {
                    avatarControllers[point.x][point.y].rootView.setVisibility(View.VISIBLE);
                }


                for (final Map.Entry<String, Point> stringPointEntry : userPositions.entrySet()) {
                    final Point point = stringPointEntry.getValue();
                    final AvatarController avatarController = avatarControllers[point.x][point.y];

                    final User user = userObjects.get(stringPointEntry.getKey());
                    avatarController.rootView.setVisibility(View.VISIBLE);
                    avatarController.setMood(user.getMood());
                    avatarController.setFaceColor(user.getColor());
                    avatarController.setName(user.getName());


                }

                h.postDelayed(this, 500);
            }
        });
    }

    private Point getRandomPoint() {
        Random r = new Random();
        final Point point = new Point(Math.abs(r.nextInt() % SIZE), Math.abs(r.nextInt() % SIZE));

        if (point.equals(new Point(CENTER,CENTER))) {
            return getRandomPoint(); // try again ;-)
        }
        return point;
    }

    private HashMap<String, User> userObjects = new HashMap<>();
    private HashMap<String, Point> userPositions = new HashMap<>();

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
