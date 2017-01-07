package iut_lry.coursedorientation;

import android.content.Intent;

/**
 * Created by Baptiste on 01/12/2016. aa
 */
public interface IFragmentToActivity {
    void showToast(String msg);

    void afficherProgressBar();
    void cacherProgressBar();

    void communicateToFragment2();

    void communicateToFragment3();
}
