package sleeping_vityaz.trackmycaffeine.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.github.lzyzsd.circleprogress.ArcProgress;

import sleeping_vityaz.trackmycaffeine.databases.DBTools;
import sleeping_vityaz.trackmycaffeine.R;
import sleeping_vityaz.trackmycaffeine.util.Calculations;
import sleeping_vityaz.trackmycaffeine.util.CommonConstants;
import sleeping_vityaz.trackmycaffeine.util.Util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerViewAdapter.SimpleViewHolder> {

    DBTools dbTools = null;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView tv_keyId, tv_caffeine_mass, tv_product, tv_date, tv_time, tv_drink_volume;
        Button buttonDelete;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tv_keyId = (TextView) itemView.findViewById(R.id.key_id);
            tv_caffeine_mass = (TextView) itemView.findViewById(R.id.tv_caffeine_mass);
            tv_product = (TextView) itemView.findViewById(R.id.tv_product);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_drink_volume = (TextView) itemView.findViewById(R.id.tv_drink_volume);
            buttonDelete = (Button) itemView.findViewById(R.id.delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(getClass().getSimpleName(), "onItemSelected: " + tv_product.getText().toString());
                    Toast.makeText(view.getContext(), "onItemSelected: " + tv_product.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Context mContext;
    private ArrayList<HashMap<String, String>> mDataset;
    private Calendar calendar;
    private DateFormat dateFormat;
    private double caffeineConsumedToday;

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public RecyclerViewAdapter(Context context, ArrayList<HashMap<String, String>> objects) {
        this.mContext = context;
        this.mDataset = objects;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        HashMap<String, String> recordMap = mDataset.get(position);
        dbTools = DBTools.getInstance(mContext);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mItemManger.closeAllItems();
                dbTools.deleteRecord(viewHolder.tv_keyId.getText().toString());

                calendar = Calendar.getInstance();
                dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

                ArrayList<HashMap<String, String>> allRecordsOnThisDate = dbTools.getAllRecordsOnThisDate(Util.convertDateForDB(dateFormat.format(calendar.getTime())));
                caffeineConsumedToday = 0;
                for (HashMap<String, String> hashMap : allRecordsOnThisDate){
                    caffeineConsumedToday += Double.parseDouble(hashMap.get(CommonConstants.CAFFEINE_MASS));
                }

                //arcProgress.setProgress((int) Calculations.round(caffeineConsumedToday, 0));
                Toast.makeText(view.getContext(), "Deleted " + viewHolder.tv_product.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.tv_keyId.setText(recordMap.get(CommonConstants.KEY_ID));
        viewHolder.tv_caffeine_mass.setText(recordMap.get(CommonConstants.CAFFEINE_MASS)+"mg from ");
        viewHolder.tv_product.setText(recordMap.get(CommonConstants.PRODUCT));
        viewHolder.tv_date.setText(Util.convertDateFromDB(recordMap.get(CommonConstants.DATE_CREATED)));
        viewHolder.tv_time.setText(Util.convertTimeFromDB(recordMap.get(CommonConstants.TIME_STARTED)));
        viewHolder.tv_drink_volume.setText(" - "+recordMap.get(CommonConstants.DRINK_VOLUME)+"fl.oz.");
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }
}
