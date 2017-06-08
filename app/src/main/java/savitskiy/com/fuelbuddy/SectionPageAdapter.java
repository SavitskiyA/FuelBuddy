package savitskiy.com.fuelbuddy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.List;

/**
 * Created by Andrey on 05.06.2017.
 */

public class SectionPageAdapter extends FragmentPagerAdapter {
    private List<GasModel> gasModels;
    private Context context;


    private FragmentListByDistance fragmentListByDistance;
    private FragmentListByPrice fragmentListByPrice;

    public SectionPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public SectionPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        fragmentListByDistance = new FragmentListByDistance();
        fragmentListByPrice = new FragmentListByPrice();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return fragmentListByDistance;
            case 1:
                return fragmentListByPrice;
        }
        return null;

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.by_distance);
            case 1:
                return context.getResources().getString(R.string.by_cost);
        }
        return null;
    }

    public Fragment getFragment(int position) {
        switch (position) {
            case 0:
                return fragmentListByDistance;
            case 1:
                return fragmentListByPrice;
        }
        return null;
    }

}
