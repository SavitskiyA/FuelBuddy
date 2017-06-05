package savitskiy.com.fuelbuddy;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Andrey on 05.06.2017.
 */

public class SectionPageAdapter extends FragmentStatePagerAdapter {
    private List<GasModel> gasModels;
    private Context context;

    public SectionPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public SectionPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentListByDistance();
            case 1:
                return new FragmentListByPrice();
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


    @Override
    public int getItemPosition(Object object) {
        if (object instanceof FragmentListByDistance) {
            return 0;
        } else if (object instanceof FragmentListByDistance) {
            return 1;
        } else {
            return POSITION_NONE;
        }
    }

}
