package com.njnu.kai.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import com.njnu.kai.test.expandablelist.ActionExpandableListView;
import com.njnu.kai.test.support.ToastUtils;

public class ActionExpandableListActivity extends Activity {
    View menuView1, menuView2;

    @Override
    public void onCreate(Bundle savedData) {

        super.onCreate(savedData);
        // set the content view for this activity, check the content view xml file
        // to see how it refers to the ActionSlideExpandableListView view.
        this.setContentView(R.layout.main_action_expandable_list);
        // get a reference to the listview, needed in order
        // to call setItemActionListener on it
        final ActionExpandableListView list = (ActionExpandableListView)this.findViewById(R.id.list);
        // fill the list with data
        list.setAdapter(buildDummyData(), R.id.expandable_toggle_button, R.id.expandable);

        // listen for events in the two buttons for every list item.
        // the 'position' var will tell which list item is clicked
        list.setItemActionListener(new ActionExpandableListView.OnActionClickListener() {

            @Override
            public void onClick(View listView, View buttonview, int position) {
                menuView1 = listView.findViewById(R.id.menu1);
                menuView2 = listView.findViewById(R.id.menu2);
                /**
                 * Normally you would put a switch
                 * statement here, and depending on
                 * view.getId() you would perform a
                 * different action.
                 */
                String actionName = "";
                if (buttonview.getId() == R.id.buttonA) {
                    actionName = "buttonA";
                } else {
                    actionName = "ButtonB";
                }
                if (buttonview.getId() == R.id.btn_more) {
                    actionName = "button More";
                    menuView1.setVisibility(View.GONE);
                    menuView2.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(ActionExpandableListActivity.this, R.anim.push_right_in);
                    menuView2.startAnimation(animation);
                } else if (buttonview.getId() == R.id.btn_back) {
                    actionName = "button back";
                    menuView1.setVisibility(View.VISIBLE);
                    menuView2.setVisibility(View.GONE);
                    Animation animation = AnimationUtils.loadAnimation(ActionExpandableListActivity.this, R.anim.push_left_in);
                    menuView1.startAnimation(animation);
                } else {
                    list.collapse();
                }
                /**
                 * For testing sake we just show a toast
                 */
//				Toast.makeText(
//					SlideExpandableActivity.this,
//					"Clicked Action: "+actionName+" in list item "+position,
//					Toast.LENGTH_SHORT
//				).show();
            }

            // note that we also add 1 or more ids to the setItemActionListener
            // this is needed in order for the listview to discover the buttons
        }, R.id.buttonA, R.id.buttonB, R.id.btn_more, R.id.btn_back);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.showToast(ActionExpandableListActivity.this, "Item " + position + " clicked!");
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.showToast(ActionExpandableListActivity.this, "onItemLongClick " + position + " clicked!");
                return true;
            }
        });
    }

    /**
     * Builds dummy data for the test.
     * In a real app this would be an adapter
     * for your data. For example a CursorAdapter
     */
    public ListAdapter buildDummyData() {
        final int SIZE = 20;
        String[] values = new String[SIZE];
        for (int i = 0; i < SIZE; i++) {
            values[i] = "Song " + i;
        }
        return new ArrayAdapter<String>(
                this,
                R.layout.item_action_expandable_list,
                R.id.text,
                values
        );
    }
}
