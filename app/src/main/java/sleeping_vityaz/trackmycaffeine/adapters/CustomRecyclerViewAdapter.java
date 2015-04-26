package sleeping_vityaz.trackmycaffeine.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import sleeping_vityaz.trackmycaffeine.EditCustomRecord;
import sleeping_vityaz.trackmycaffeine.EditRecord;
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

public class CustomRecyclerViewAdapter extends RecyclerSwipeAdapter<CustomRecyclerViewAdapter.SimpleViewHolder> {

    DBTools dbTools = null;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView tv_keyId, tv_caffeine_mass, tv_product, tv_drink_volume;
        ImageView iv_edit, iv_delete;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tv_keyId = (TextView) itemView.findViewById(R.id.key_id);
            tv_caffeine_mass = (TextView) itemView.findViewById(R.id.tv_caffeine_mass);
            tv_product = (TextView) itemView.findViewById(R.id.tv_product);
            tv_drink_volume = (TextView) itemView.findViewById(R.id.tv_drink_volume);
            iv_edit = (ImageView) itemView.findViewById(R.id.iv_edit);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);

           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(getClass().getSimpleName(), "onItemSelected: " + tv_product.getText().toString());
                    Toast.makeText(view.getContext(), "onItemSelected: " + tv_product.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }

    private Context mContext;
    private ArrayList<HashMap<String, String>> mDataset;

    private SharedPreferences settings;
    private String units;

    public CustomRecyclerViewAdapter(Context context, ArrayList<HashMap<String, String>> objects) {
        this.mContext = context;
        this.mDataset = objects;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recyclerview_item, parent, false);

        // Restore preferences
        if (this != null) {
            settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        }
        units = settings.getString("units_drinks_pref", "");


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
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.iv_delete));
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.iv_edit));
            }
        });
        /*viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });*/
        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mItemManger.closeAllItems();
                dbTools.deleteCustomRecord(viewHolder.tv_keyId.getText().toString());

                Toast.makeText(view.getContext(), "Deleted " + viewHolder.tv_product.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyValue = viewHolder.tv_product.getText().toString();
                Intent theIntent = new Intent(mContext, EditCustomRecord.class);
                theIntent.putExtra("product", keyValue);
                theIntent.putExtra("keyId", viewHolder.tv_keyId.getText().toString());
                //Toast.makeText(view.getContext(), "Update " + viewHolder.tv_product.getText().toString() + "!", Toast.LENGTH_SHORT).show();
                mContext.startActivity(theIntent);
            }
        });
        viewHolder.tv_keyId.setText(recordMap.get(CommonConstants.C_POSITION));
        viewHolder.tv_caffeine_mass.setText(" "+recordMap.get(CommonConstants.C_MASS_CAFFEINE)+"mg");
        viewHolder.tv_product.setText(recordMap.get(CommonConstants.C_PRODUCT));

        if (units.equals("fl oz")) viewHolder.tv_drink_volume.setText(" / "+recordMap.get(CommonConstants.C_VOLUME_DRINK)+" fl.oz.");
        else viewHolder.tv_drink_volume.setText(" / "+Calculations.round((Double.parseDouble(recordMap.get(CommonConstants.C_VOLUME_DRINK)) / 0.033814), 1)+" ml");



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
