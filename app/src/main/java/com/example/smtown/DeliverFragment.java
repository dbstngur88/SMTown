package com.example.smtown;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.CALL_PHONE;
import static com.example.smtown.RemoteService.BASE_URL;

public class DeliverFragment extends Fragment {

    Retrofit retrofit;
    RemoteService remoteService;
    EditText editSearch;
    List<RestaurantVO> arrayDeliver = new ArrayList<>();
    DeliverAdapter deliverAdapter;
    ArrayList<String> deliverlist = new ArrayList<>();

    RecyclerView listDeliver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_deliver, container, false);
        editSearch = view.findViewById(R.id.editSearch);
        listDeliver = view.findViewById(R.id.listDeliver);
        listDeliver.setLayoutManager(new LinearLayoutManager(getActivity()));
        deliverAdapter = new DeliverAdapter();
        listDeliver.setAdapter(deliverAdapter);

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        remoteService=retrofit.create(RemoteService.class);

        Call<List<RestaurantVO>> call = remoteService.listDeliver();
        call.enqueue(new Callback<List<RestaurantVO>>() {
            @Override
            public void onResponse(Call<List<RestaurantVO>> call, Response<List<RestaurantVO>> response) {
                arrayDeliver=response.body();
                deliverAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<RestaurantVO>> call, Throwable t) {

            }
        });

        ImageView btnSearch = view.findViewById(R.id.btnSearch);
        final EditText editSearch = view.findViewById(R.id.editSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getSearch = editSearch.getText().toString();
                searchData(getSearch);
            }
        });

        editSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            String getSearch = editSearch.getText().toString();
                            searchData(getSearch);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });



        return view;
    }

    //LocalAdapter
    class DeliverAdapter extends RecyclerView.Adapter<DeliverAdapter.ViewHolder> {
        @NonNull
        @Override
        public DeliverAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DeliverAdapter.ViewHolder holder, final int position) {
            deliverlist.add(arrayDeliver.get(position).getName());
            holder.title.setText(arrayDeliver.get(position).getName());
            holder.category.setText(arrayDeliver.get(position).getCategory());
            holder.phone.setText(arrayDeliver.get(position).getTel());
            holder.place.setText(arrayDeliver.get(position).getAddress());
            holder.maps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra("y", Double.parseDouble(arrayDeliver.get(position).getLatitude()));     //경도
                    intent.putExtra("x", Double.parseDouble(arrayDeliver.get(position).getLongitude()));      //위도
                    intent.putExtra("place_name", arrayDeliver.get(position).getName());
                    startActivity(intent);
                }
            });
            holder.phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+arrayDeliver.get(position).getTel()));
                    if(ContextCompat.checkSelfPermission(getActivity(),CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                        startActivity(intent);
                    } else requestPermissions(new String[]{CALL_PHONE}, 1);
                }
            });
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("place_name",arrayDeliver.get(position).getName());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayDeliver.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView maps,chat;
            TextView title, category, phone, place;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                category = itemView.findViewById(R.id.category);
                phone = itemView.findViewById(R.id.phone);
                place = itemView.findViewById(R.id.place);
                maps = itemView.findViewById(R.id.image);
                chat = itemView.findViewById(R.id.chat);
            }
        }
    }

    public void searchData(String query){
        Call<List<RestaurantVO>> call = remoteService.schDeliver(query);
        call.enqueue(new Callback<List<RestaurantVO>>() {
            @Override
            public void onResponse(Call<List<RestaurantVO>> call, Response<List<RestaurantVO>> response) {
                arrayDeliver=response.body();
                deliverAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<RestaurantVO>> call, Throwable t) {

            }
        });

    }
}