package net.glochat.dev.activity;
import android.content.Intent;
import android.os.Bundle;
import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import net.glochat.dev.R;
import java.util.ArrayList;
import java.util.List;

public class OnboardScreen extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("", getString(R.string.onboard_text_first), R.drawable.ic_undraw_online_video);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("", getString(R.string.onboard_text_second), R.drawable.ic_undraw_online_messaging);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("", getString(R.string.onboard_text_third), R.drawable.ic_undraw_camera);
        AhoyOnboarderCard ahoyOnboarderCard4 = new AhoyOnboarderCard("", getString(R.string.onboard_text_fourth), R.drawable.ic_undraw_calling);

        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard4.setBackgroundColor(R.color.black_transparent);


        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);
        pages.add(ahoyOnboarderCard4);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.white);
            page.setDescriptionColor(R.color.grey_200);
        }

        // setImageBackground(R.drawable.onboard_img);
        setGradientBackground();
        showNavigationControls(true);
        setFinishButtonTitle("Get Started");
        setInactiveIndicatorColor(R.color.grey_600);
        setActiveIndicatorColor(R.color.white);
        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }


}