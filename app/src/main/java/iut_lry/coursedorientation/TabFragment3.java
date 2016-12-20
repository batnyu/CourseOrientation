package iut_lry.coursedorientation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class TabFragment3 extends Fragment implements View.OnClickListener {
    private IFragmentToActivity mCallback;

    private TextView mTextView1;
    private Button btnFtoA;

    DBController controller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        /*mTextView1 = (TextView) view.findViewById(R.id.textView1);
        btnFtoA = (Button) view.findViewById(R.id.button);
        btnFtoA.setOnClickListener(this);*/

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //updateList();

    }

    public void updateList() {

        controller = new DBController(getActivity());
        // create the grid item mapping
        String[] from = new String[] {"RIEN", "balise", "temps", "RIEN"};
        int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };

        ArrayList<HashMap<String, String>> userList;
        // Get User records from SQLite DB

        userList = controller.getAllUsers();
        // If users exists in SQLite DB
        if (userList.size() != 0) {

            // Set the User Array list in ListView
            ListAdapter adapter = new SpecialAdapter(getActivity(), userList, R.layout.grid_item, from, to);
            ListView myList = (ListView) getActivity().findViewById(R.id.listview);
            myList.setAdapter(adapter);
            registerForContextMenu(myList);
        }
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

    public void fragmentCommunication() {

        mTextView1.setText("Hello from Tab Fragment 1");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                mCallback.showToast("Hello from Fragment 2");
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updateList();
        }
        else {
        }
    }

}