package com.ioannisnicos.ethiomoviesuser.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.ioannisnicos.ethiomoviesuser.R;
import com.ioannisnicos.ethiomoviesuser.adapter.SubscriptionsRecyclerAdapter;
import com.ioannisnicos.ethiomoviesuser.models.Stores;
import com.ioannisnicos.ethiomoviesuser.models.StoresAdditionalStatus;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddSubscriptionDialog extends AppCompatDialogFragment {

    private List<StoresAdditionalStatus> mNearUserStoresSubsStatus;
    private List<Stores>                   mNearUsersStores;

    private SubscriptionsRecyclerAdapter mNearUserSubsAdapter;
    private RecyclerView mNearUserSubsRecyclerView;

    private OnNearUserStoreStatusListener listener;
    public interface OnNearUserStoreStatusListener {
        void UnsubscribeStore(String userGId,int storeId, int position);
        void subscribeStore  (String userGId,int storeId, int position);
    }

    public void setOnNearUserStoreStatusListener(OnNearUserStoreStatusListener listener){
        this.listener =  listener;
    };


    public AddSubscriptionDialog(List<StoresAdditionalStatus> mNearUserStoresSubsStatus, List<Stores> mNearUsersStores) {
        this.mNearUserStoresSubsStatus = mNearUserStoresSubsStatus;
        this.mNearUsersStores = mNearUsersStores;
    }


    public void notifyDataSetChangedNearUserSubsAdapter() {
        this.mNearUserSubsAdapter.notifyDataSetChanged();
    }

    public void notifyItemChangedNearUserSubsAdapter(int p) {
        this.mNearUserSubsAdapter.notifyItemChanged(p);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_subscribe_subscription_activity, null);
        builder.setView(view);


        mNearUserSubsRecyclerView = view.findViewById(R.id.recycler_view_new_subscriptions);


        mNearUserSubsAdapter = new SubscriptionsRecyclerAdapter(getContext(), mNearUsersStores,mNearUserStoresSubsStatus);
        mNearUserSubsRecyclerView.setAdapter(mNearUserSubsAdapter);
        mNearUserSubsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNearUserSubsRecyclerView.setHasFixedSize(true);

        mNearUserSubsAdapter.setOnSubscribeButtonClickListener(
                (position -> {
                   this.setCancelable(false);
                    //delete
                    if( mNearUserStoresSubsStatus.get(position).getSubscription_status()==null)
                        listener.subscribeStore(mNearUserStoresSubsStatus.get(position).getGoogle_id(),mNearUserStoresSubsStatus.get(position).getId(),position);
                        //onMyItemClickMain(position);
                    else listener.UnsubscribeStore(mNearUserStoresSubsStatus.get(position).getGoogle_id(),mNearUserStoresSubsStatus.get(position).getId(),position);
                        //mNearUserStoresSubsStatus.get(position).setSubscription_status(null);
                        //mNearUserSubsAdapter.notifyItemChanged(position);


                    //UnsubscribeStore(myGoogleId, mUsersStores.get(position).getId(), position);

                }));
        mNearUserSubsAdapter.setOnSubscriptionBKClickListener
                (position -> {
                    this.setCancelable(false);

                    listener.subscribeStore(mNearUserStoresSubsStatus.get(position).getGoogle_id(),mNearUserStoresSubsStatus.get(position).getId(),position);
                    Toast.makeText(getContext(), "open acctivity " + position, Toast.LENGTH_SHORT).show();


                    //Toast.makeText(getBaseContext(), "open acctivity " + position, Toast.LENGTH_SHORT).show();
                    //startActivity(StoreMediaActivity.newInstance(MyMovieStoresActivity.this, mMyStores.get(position).getId()));
                });


        return builder.create();
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            listener = (OnNearUserStoreStatusListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() +
//                    "must implement ExampleDialogListener");
//        }
//    }




}
