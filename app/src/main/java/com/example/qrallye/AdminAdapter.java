package com.example.qrallye;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdminAdapter extends ArrayAdapter<Administrators> {
    private int resourceLayout;
    private Context context;
    private List<Administrators> adminList;

    public AdminAdapter(Context context, List<Administrators> adminList) {
        super(context, R.layout.admin_list_item, adminList);
        this.context = context;
        this.adminList = adminList;
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent)
    {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.admin_list_item, null, true);
        }

        // Création d'un view holder pour recycler les vues et rendre la liste plus fluide
        ViewHolder holder = (ViewHolder) convertView.getTag();

        if(holder == null){
            holder = new ViewHolder();
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_number = convertView.findViewById(R.id.tv_number);
            holder.phone = convertView.findViewById(R.id.phone);
            convertView.setTag(holder);
        }

        //<editor-fold desc="Affichage des données">
        final Administrators administrators = adminList.get(position);
        holder.tv_name.setText(administrators.getUsername());
        holder.tv_number.setText(administrators.getNumber());
        holder.phone.setImageResource(R.drawable.ic_contact);
        //</editor-fold>

        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:"+administrators.getNumber()));
                phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(phoneIntent);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        TextView tv_name;
        TextView tv_number;
        ImageView phone;
    }
}
