package iut_lry.coursedorientation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class TabFragment3 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;

    DBController controller;
    LinearLayout interfaceMain;
    TextView noParcours;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        return view;
    }

    public void updateList() {
        //TEST
        interfaceMain = (LinearLayout) getActivity().findViewById(R.id.main);
        noParcours = (TextView) getActivity().findViewById(R.id.noParcours);

        controller = new DBController(getActivity());
        // create the grid item mapping
        String[] from = new String[] {"balise", "temps", "RIEN", "RIEN"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };

        ArrayList<HashMap<String, String>> userList;
        // Get User records from SQLite DB

        userList = controller.getAllUsers();
        // If users exists in SQLite DB
        if (userList.size() != 0) {

            //Test
            interfaceMain.setVisibility(LinearLayout.VISIBLE);
            noParcours.setVisibility(LinearLayout.GONE);

            // Set the User Array list in ListView
            ListAdapter adapter = new SpecialAdapter(getActivity(), userList, R.layout.grid_item, from, to);
            ListView myList = (ListView) getActivity().findViewById(R.id.listview);
            myList.setAdapter(adapter);
            registerForContextMenu(myList);
        }
        else
        {
            //Test
            interfaceMain.setVisibility(LinearLayout.GONE);
            noParcours.setVisibility(LinearLayout.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //pour afficher la base déjà téléchargé quand on redémarre l'appli
        updateList();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (IFragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IFragmentToActivity");
        }
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    public void onRefresh() {
        Toast.makeText(getActivity(), "Fragment 3: Refresh called.",
                Toast.LENGTH_SHORT).show();
    }

    public void fragmentCommunication3() {

        updateList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                mCallback.showToast("Hello from Fragment 2");
                break;
        }
    }
}