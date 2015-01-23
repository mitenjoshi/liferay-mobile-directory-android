package com.rivetlogic.liferayrivet.screens.peopledirectorylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.rivetlogic.liferayrivet.R;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.liferayrivet.component.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by lorenz on 1/15/15.
 */
public class PeopleDirectoryListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<User> users;

    private ArrayList<User> filteredUsers;

    public PeopleDirectoryListAdapter(Context context) {
        this.context = context;
    }

    public void updateAdapter(ArrayList<User> users) {
        this.users = users;
        this.filteredUsers = users;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (filteredUsers == null)
            return 0;
        return filteredUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        User user = (User) getItem(position);

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lr_list_row_directory, null);

            holder.image = (ImageView) convertView.findViewById(R.id.lr_list_row_directory_image);
            holder.name = (TextView) convertView.findViewById(R.id.lr_list_row_directory_name);
            holder.title = (TextView) convertView.findViewById(R.id.lr_list_row_directory_title);
            holder.email = (TextView) convertView.findViewById(R.id.lr_list_row_directory_email);
            holder.skypeIcon = (ImageView) convertView.findViewById(R.id.lr_list_row_directory_icon_skype);
            holder.phoneIcon = (ImageView) convertView.findViewById(R.id.lr_list_row_directory_icon_phone);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(context).load(SettingsUtil.getServer() + user.portraitUrl)
                .placeholder(R.drawable.ic_list_image_default)
                .error(R.drawable.ic_list_image_default)
                .transform(new RoundedTransformation((int) (context.getResources().getDimension(R.dimen.listview_image_size) / 2), 2))
                .centerCrop()
                .resizeDimen(R.dimen.listview_image_size, R.dimen.listview_image_size)
                .into(holder.image);

        holder.name.setText(user.fullName);
        holder.title.setText(user.jobTitle);
        holder.email.setText(user.emailAddress);
        holder.phoneIcon.setVisibility(user.userPhone != null && user.userPhone.length() > 0 ? View.VISIBLE : View.GONE);
        holder.skypeIcon.setVisibility(user.skypeName != null && user.skypeName.length() > 0 ? View.VISIBLE : View.GONE);

        return convertView;
    }

    class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView title;
        public TextView email;
        public ImageView skypeIcon;
        public ImageView phoneIcon;
    }

    @Override
    public android.widget.Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults returnValues = new FilterResults();
                ArrayList<User> results = new ArrayList<>();

                if (users != null && users.size() > 0) {
                    if (constraint == null || constraint.length() == 0) {
                        returnValues.values = users;
                    } else {
                        for (User user : users) {
                            if (user.fullName.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                                    user.city.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                                    user.jobTitle.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                                    user.skypeName.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                                    user.emailAddress.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                                    user.screenName.toUpperCase().contains(constraint.toString().toUpperCase()) ||
                                    user.userPhone.toUpperCase().contains(constraint.toString().toUpperCase())
                                    )
                                results.add(user);
                        }
                        returnValues.values = results;
                    }
                }
                return returnValues;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredUsers = (ArrayList<User>) results.values;
                PeopleDirectoryListAdapter.this.notifyDataSetChanged();
            }

        };
    }

}