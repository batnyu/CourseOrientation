package iut_lry.coursedorientation;

import android.content.Intent;

/**
 * Created by Baptiste on 01/12/2016. aa
 */
public interface IFragmentToActivity {

    void showToast(String msg,String duree);

    void hideKeyboard();

    public String getWifiApIpAddress();

    public void vibrer();

    void communicateToFragment2();

    void communicateToFragment3();
}
