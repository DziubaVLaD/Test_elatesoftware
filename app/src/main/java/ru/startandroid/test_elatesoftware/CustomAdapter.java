package ru.startandroid.test_elatesoftware;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.vk.sdk.VKUIHelper.getApplicationContext;



class CustomAdapter extends BaseAdapter {
    private ArrayList<String> users,messages;
    private Context context;
    private VKList<VKApiDialog> list;

    public CustomAdapter(Context context, ArrayList<String> users, ArrayList<String> messages, VKList<VKApiDialog> list){
        this.users = users;
        this.messages = messages;
        this.context = context;
        this.list = list;
    }
    public CustomAdapter(Context context, ArrayList<String> users, ArrayList<String> messages){
        this.users = users;
        this.messages = messages;
        this.context = context;
    }


    @Override
    public int getCount() {
            return users.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
       SetData setData = new SetData();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View view = inflater.inflate(R.layout.style_list_view,null);

       SetData.user_name = (TextView) view.findViewById(R.id.user_name);
       SetData.msg = (TextView) view.findViewById(R.id.msg);

       SetData.user_name.setText(users.get(i));
       SetData.msg.setText(messages.get(i));

        if (list==null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ArrayList<String> inList = new ArrayList<>();
                    final ArrayList<String> outList = new ArrayList<>();
                    final int id = list.get(i).message.user_id;

                    VKRequest request = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID, id));
                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);

                            try {
                                JSONArray array = response.json.getJSONObject("response").getJSONArray("items");
                                VKApiMessage[] msg = new VKApiMessage[array.length()];
                                for (int j = 0; j < array.length(); j++) {
                                    VKApiMessage mes = new VKApiMessage(array.getJSONObject(j));
                                    msg[j] = mes;
                                }
                                for (VKApiMessage mess : msg) {
                                    if (mess.out) {
                                        outList.add(mess.body);
                                    } else {
                                        inList.add(mess.body);
                                    }
                                }

                                context.startActivity(new Intent(context, SendMessage.class).putExtra("id", id)
                                        .putExtra("in", inList).putExtra("out", outList));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });
        }
        return view;
    }

    public static class SetData{
       static TextView user_name,msg;
    }
}
