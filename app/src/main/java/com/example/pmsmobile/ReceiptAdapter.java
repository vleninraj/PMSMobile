package com.example.pmsmobile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.pmsmobile.Models.ReceiptList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;

public class ReceiptAdapter extends BaseAdapter implements Filterable {




    private Activity context;
    Context c;
    ArrayList<ReceiptList> _receiptList;
    ArrayList<ReceiptList> orgi;
    public ReceiptAdapter(Activity  context, ArrayList<ReceiptList> receiptList) {
        //   super(c, R.layout.listview,workid);

        this.context=context;
        this._receiptList=receiptList;

    }

    @Override
    public int getCount() {
        return _receiptList.size();
    }

    @Override
    public Object getItem(int i) {


        return _receiptList.get(i);

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View r=convertView;
        ViewHolder viewHolder=null;
        if(r==null){
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.receipt_adapter,null,true);
            viewHolder=new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{

            viewHolder=(ViewHolder) r.getTag();

        }
        final ReceiptList s= (ReceiptList) this.getItem(position);
        viewHolder.txtadmobvno.setText(s.getMobileVNo());
        viewHolder.txtadmonthyear.setText(s.getMonthYear());
        viewHolder.txtadtenantname.setText(s.getTenantName());
        viewHolder.txtadAmount.setText(String.format(Common.format,Double.valueOf(s.getAmount())));

        return r;



    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                final  FilterResults oReturn=new FilterResults();
                final ArrayList<ReceiptList> results=new ArrayList<>();

                if(orgi==null)orgi=_receiptList;

                if(constraint!=null){
                    if(orgi!=null && orgi.size()>0){
                        for(final ReceiptList g:orgi){
                            if(g.getTenantName().toLowerCase().contains(constraint.toString()))results.add(g);
                        }
                    }
                    oReturn.values=results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                _receiptList= (ArrayList<ReceiptList>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class ViewHolder
    {

        TextView txtadmobvno,txtadtenantname,txtadAmount,txtadmonthyear;


        ViewHolder(View v)
        {

            txtadmobvno=(TextView) v.findViewById(R.id.txtadmobvno);
            txtadtenantname=(TextView) v.findViewById(R.id.txtadtenantname);
            txtadAmount=(TextView) v.findViewById(R.id.txtadAmount);
            txtadmonthyear=(TextView) v.findViewById(R.id.txtadmonthyear);
        }

    }
}
