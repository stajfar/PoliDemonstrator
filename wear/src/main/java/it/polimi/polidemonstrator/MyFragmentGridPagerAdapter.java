package it.polimi.polidemonstrator;

import android.app.Fragment;
import android.content.Context;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.List;

/**
 * Created by saeed on 7/6/2016.
 */

public class MyFragmentGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;
    private List mRows;

    public MyFragmentGridPagerAdapter(Context context, android.app.FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // A simple container for static data in each page
    private static class Page {
        // static resources
        int titleRes;
        int textRes;
        int iconRes;

        public Page(int iconRes, int textRes, int titleRes) {
            this.iconRes = iconRes;
            this.textRes = textRes;
            this.titleRes = titleRes;
        }
    }

    // Create a static set of pages in a 2D array
    Page page1=new Page(1,1,0);
    Page page2=new Page(1,1,0);
    Page page3=new Page(1,1,0);
    Page[] pages={page1,page2,page3};


    private final Page[][] PAGES = {pages


    };

    @Override
    public Fragment getFragment(int row, int col) {
        Page page = PAGES[row][col];
        String title ="title";
               // page.titleRes != 0 ? mContext.getString(page.titleRes) : null;
        String text ="page Text";
                //page.textRes != 0 ? mContext.getString(page.textRes) : null;
        CardFragment fragment = CardFragment.create(title, text);

        // Advanced settings (card gravity, card expansion/scrolling)
       // fragment.setCardGravity(page.cardGravity);
        //fragment.setExpansionEnabled(page.expansionEnabled);
        //fragment.setExpansionDirection(page.expansionDirection);
       // fragment.setExpansionFactor(page.expansionFactor);
        return fragment;
    }

    @Override
    public int getRowCount() {
        return PAGES.length;
    }

    @Override
    public int getColumnCount(int rowNum) {
        return PAGES[rowNum].length;
    }
}