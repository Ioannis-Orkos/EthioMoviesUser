package com.ioannisnicos.ethiomoviesuser.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ioannisnicos.ethiomoviesuser.R;
import com.ioannisnicos.ethiomoviesuser.models.Stores;
import com.ioannisnicos.ethiomoviesuser.models.StoresAdditionalStatus;
import com.ioannisnicos.ethiomoviesuser.retrofit.ApiClient;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import de.hdodenhof.circleimageview.CircleImageView;

public class RentMovieAdapter extends RecyclerView.Adapter<RentMovieAdapter.RentMovieHolder> {

    private WeakReference<Context> mContext;
    private List<StoresAdditionalStatus> mStoresMovieStatus;
    private List<Stores> mStores;

    private OnItemClickListener mListener;

    HashMap<Integer, RentMovieHolder > mHolderMap=new HashMap<Integer,RentMovieAdapter.RentMovieHolder>();


    public interface OnItemClickListener {
        void onItemClick(int position,String msg);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
         mListener = listener;
    }



    public RentMovieAdapter(Context context,List<Stores> stores,List<StoresAdditionalStatus> storesMovieStatus) {
        mContext = new WeakReference<>(context);
        mStores = stores;
        mStoresMovieStatus = storesMovieStatus;
    }

    @Override
    public RentMovieAdapter.RentMovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RentMovieAdapter.RentMovieHolder(LayoutInflater.from(mContext.get()).inflate(R.layout.card_rent_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(RentMovieAdapter.RentMovieHolder holder, int position) {

        mHolderMap.put(position,holder);

        holder.mProgressBar.setVisibility(View.GONE);
        if(mStores.get(position).getBanner()!=null)


            Glide.with(mContext.get()).load(ApiClient.BASE_URL+mStores.get(position).getBanner())
                                  .diskCacheStrategy(DiskCacheStrategy.ALL)


                                  .into(new CustomTarget<Drawable>() {
                                     @Override
                                     public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                         holder.rentLayoutBackGround.setBackground(resource);
                                     }
                                     @Override
                                     public void onLoadCleared(@Nullable Drawable placeholder) {

                                     }
                                 });
        Glide.with(mContext.get()).load(ApiClient.BASE_URL+mStores.get(position).getProf_img())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.profilePictureImage);


        holder.businessNameText.setText(mStores.get(position).getBusiness_name());
        holder.storeNameText.setText(mStores.get(position).getDisplay_name());
        holder.descriptionText.setText(mStores.get(position).getDescription());
        holder.textReqMesg.setText(mStoresMovieStatus.get(position).getReq_user_msg());

        String aval = mStoresMovieStatus.get(position).getMovie_available();
        if(aval.equals("no")) {aval = "Not Avalailable";  holder.movieAvailableText.setBackgroundColor(Color.RED); }
        else  { holder.movieAvailableText.setBackgroundColor(Color.GREEN);     aval = "Avalailable";}

        holder.movieAvailableText.setText(aval);

        if( mStoresMovieStatus.get(position).getMovie_status()==null && mStoresMovieStatus.get(position).getMovie_available()==null )    {
            holder.rentButton.setText("Sent request");

        }
        else if( mStoresMovieStatus.get(position).getMovie_status()==null && mStoresMovieStatus.get(position).getMovie_available() !=null )    {
            holder.rentButton.setText("Rent");

           }
        else if( mStoresMovieStatus.get(position).getMovie_status().equals("ready") &&mStoresMovieStatus.get(position).getMovie_available()!=null )    {
            holder.rentButton.setText("Ready for collection");
            holder.rentButton.setEnabled(false);
            holder.rentButton.setTextColor(Color.GREEN); }
        else if( mStoresMovieStatus.get(position).getMovie_status().equals("pending") &&mStoresMovieStatus.get(position).getMovie_available()==null )    {
            holder.textReqMesg.setEnabled(false);
            holder.rentButton.setText("Cancel Request");
                    }
            //holder.rentButton.setEnabled(false);
        else if( mStoresMovieStatus.get(position).getMovie_status().equals("pending") &&mStoresMovieStatus.get(position).getMovie_available()!=null )    {
            holder.textReqMesg.setEnabled(false);
            holder.rentButton.setText("Cancel Request");
        }

        //holder.rentButton.setEnabled(false);
        else if ( mStoresMovieStatus.get(position).getMovie_status().equals("rejected") &&mStoresMovieStatus.get(position).getMovie_available()!=null )    {
            holder.rentButton.setText("Request Rejected");
            holder.rentButton.setEnabled(false);       }

    }

    public RentMovieAdapter.RentMovieHolder  getHolder(int position) {
        return mHolderMap.get(position);
    }


        @Override
    public int getItemCount() {
        return mStores.size();
    }


    public class RentMovieHolder extends RecyclerView.ViewHolder {

        public TextView movieAvailableText;
        public TextView businessNameText;
        public TextView storeNameText;
        public TextView descriptionText;
        public CircleImageView profilePictureImage;
        public Button rentButton;
        public ConstraintLayout rentLayoutBackGround;
        public TextInputEditText textReqMesg;
        public ProgressBar mProgressBar;


        public RentMovieHolder(View itemView) {
            super(itemView);

          mProgressBar = itemView.findViewById(R.id.progressBar_rent_dialog_adapter);
          rentLayoutBackGround =  itemView.findViewById(R.id.layout_rent_store);
          profilePictureImage =  itemView.findViewById(R.id.circle_img_rent_storeprof);
          storeNameText = itemView.findViewById(R.id.textView_rent_store_store_user_name);
          movieAvailableText = itemView.findViewById(R.id.textView_rent_movie_availability);
          businessNameText = itemView.findViewById(R.id.textView_rent_store_business_name);
          descriptionText =   itemView.findViewById(R.id.textView_rent_store_description);
          rentButton= itemView.findViewById(R.id.button_rent_movie);
          textReqMesg= itemView.findViewById(R.id.textInput_rent_store_req_msg);



          rentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position,textReqMesg.getText().toString());
                        }
                    }
                }
            });

        }
    }

}
