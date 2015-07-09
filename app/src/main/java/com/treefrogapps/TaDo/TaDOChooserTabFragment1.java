package com.treefrogapps.TaDo;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;


public class TaDOChooserTabFragment1 extends Fragment implements View.OnClickListener {

    private View rootView;
    private ImageButton taDOChooserFragmentMenuButton;


    public TaDOChooserTabFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_tado_chooser_tab_fragment1, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initialiseInputs();
    }

    private void initialiseInputs() {

        taDOChooserFragmentMenuButton = (ImageButton) rootView.findViewById(R.id.taDOChooserFragment1MenuButton);
        taDOChooserFragmentMenuButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == taDOChooserFragmentMenuButton.getId()){
            inflatePopMenu(v);
        }
    }

    private void inflatePopMenu(View v) {
        //create popUpMenu (context menu)
        Context style = new ContextThemeWrapper(getActivity(), R.style.PopUpMenu);
        PopupMenu popUpMenu = new PopupMenu(style, v);
        // inflate my context menu xml layout
        MenuInflater inflater = popUpMenu.getMenuInflater();
        inflater.inflate(R.menu.fragment_tado_chooser_popmenu, popUpMenu.getMenu());

        popUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){

                    case R.id.popmenu_tado_chooser_notify :

                        return true;

                    case R.id.popmenu_tado_chooser_done :
                        item.setChecked(true);
                        return true;

                    case R.id.popmenu_tado_chooser_delete :
                        item.setChecked(true);
                        return true;

                    case R.id.popmenu_tado_chooser_do_not_alter :
                        item.setChecked(true);
                        return true;

                    default:return false;
                }
            }
        });

        popUpMenu.show();
    }
}
