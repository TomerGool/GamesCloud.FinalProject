package com.example.android1finalproject.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.android1finalproject.R;
import com.example.android1finalproject.models.Likes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameDescription extends Fragment {

    private ImageView imageView;
    private TextView titleTextView;
    private TextView downloadsTextView;
    private TextView likesCount;

    private Boolean isLiked;
    FirebaseFirestore db;
    FirebaseAuth auth;
    public static String name;
    private String added;
    private String image;
    private ArrayList<String> genres;
    private ArrayList<String> platform;

    Map<String,Object> likeData;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public GameDescription() {
        // Required empty public constructor
    }

    public static GameDescription newInstance(String param1, String param2) {
        GameDescription fragment = new GameDescription();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("name");
            added = getArguments().getString("downloads");
            image = getArguments().getString("image");
            platform = getArguments().getStringArrayList("platforms");
            genres = getArguments().getStringArrayList("genres");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_description, container, false);
        auth = FirebaseAuth.getInstance();
        imageView = view.findViewById(R.id.image);
        titleTextView = view.findViewById(R.id.title);
        downloadsTextView = view.findViewById(R.id.downloads);
        likesCount = view.findViewById(R.id.likes);
        db = FirebaseFirestore.getInstance();
        likeData = new HashMap<>();

        Bundle args = getArguments();
        if (args != null) {
            String imageUrl = args.getString("image");
            String name = args.getString("name");
            String downloads = args.getString("downloads");

            if (imageUrl != null) {
                Glide.with(this)
                        .load(imageUrl)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.error))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
            }

            titleTextView.setText(name);
            downloadsTextView.setText(downloads);
            showLikesCounter();

            TextView platformsTextView = view.findViewById(R.id.platforms);
            TextView genresTextView = view.findViewById(R.id.genres);

            if (platform != null && genres != null) {
                String platformsText = TextUtils.join(", ", platform);
                String genresText = TextUtils.join(", ", genres);

                platformsTextView.setText(platformsText);
                genresTextView.setText(genresText);
            }
        }

        view.findViewById(R.id.likeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userId = auth.getCurrentUser().getUid();
                DocumentReference gameRef = db.collection("games").document(name);

                db.collection("users").document(userId).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    DocumentReference likeRef = gameRef.collection("Likes").document(userId);
                                    DocumentReference userLike = db.collection("users").document(userId).collection("Likes").document(name);


                                    likeRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists())
                                            {
                                                isLiked = false;
                                                likeRef.delete();
                                                userLike.delete();
                                                Toast.makeText(requireContext(), R.string.unlike, Toast.LENGTH_SHORT).show();
                                                showLikesCounter();
                                                view.findViewById(R.id.likeButton).setBackgroundResource(R.drawable.unpressed_like_button);

                                            }
                                            else
                                            {
                                                isLiked = true;
                                                likeData.put("Timestamp", FieldValue.serverTimestamp());
                                                likeData.put("username",task.getResult().getString("username"));
                                                likeData.put("game", name);
                                                likeData.put("userId",userId);
                                                likeData.put("isLikes",isLiked);
                                                likeData.put("image",image);
                                                likeData.put("added",added);
                                                likeData.put("genres",genres);
                                                likeData.put("platform",platform);
                                                view.findViewById(R.id.likeButton).setBackgroundResource(R.drawable.pressed_like_button);


                                                likeRef.set(likeData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(requireContext(), R.string.like, Toast.LENGTH_SHORT).show();
                                                        userLike.set(likeData);
                                                        showLikesCounter();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                    }
                                                });
                                            }

                                        }
                                    });
                                }

                            }
                        });

            }
        });

        view.findViewById(R.id.massegeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("games").document(name).collection("Likes").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<String> uidList = new ArrayList<>();
                                for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments())
                                {
                                    uidList.add(doc.getString("userId"));
                                }
                                if(uidList.contains(auth.getUid()))
                                {
                                    UserList.gameName = name;
                                    Navigation.findNavController(view).navigate(R.id.action_gameDescription_to_chatForGame);
                                }
                                else
                                {
                                    Toast.makeText(requireContext(), getString(R.string.you_cant_continue_if_you_dont_like_this_game), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        return view;
    }
    public void showLikesCounter()
    {
        db.collection("games").document(name)
                .collection("Likes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> uidList = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                        {
                            uidList.add(doc.getId());
                        }
                        if(uidList.contains(auth.getUid()))
                        {
                            if(queryDocumentSnapshots.getDocuments().size() == 1)
                            {
                                likesCount.setText(getString(R.string.you_liked_this_game));
                            }
                            else
                            {
                                likesCount.setText(getString(R.string.you_and) + (queryDocumentSnapshots.getDocuments().size()-1)+getString(R.string.people_likes_this_game));
                            }
                        }
                        else if(queryDocumentSnapshots.size() ==0)
                        {
                            likesCount.setText(getString(R.string.no_one_likes_this_game));
                        }
                        else if(!uidList.contains(auth.getUid()))
                        {
                            likesCount.setText(queryDocumentSnapshots.size() + getString(R.string.people_likes_this_game));
                          }
                        else if(queryDocumentSnapshots.size() == 1)
                        {
                            if(uidList.contains(auth.getUid()))
                            {
                                likesCount.setText(getString(R.string.you_liked_this_game));
                            }
                            else {
                                likesCount.setText(getString(R.string.someone_likes_this_game));
                            }
                        } else if (queryDocumentSnapshots.size() == 2) {
                            if(uidList.contains(auth.getUid()))
                            {
                                likesCount.setText(getString(R.string.you_and_someone_liked_this_game));
                            }
                            else {
                                likesCount.setText(queryDocumentSnapshots.size() + getString(R.string.people_likes_this_game));
                            }
                        }
                    }
                });
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
        menu.findItem(R.id.other_action).setTitle(getString(R.string.main_screen));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(auth.getCurrentUser()!=null)
        {
            switch (item.getItemId()){
                case R.id.logout_action:
                {
                    auth.signOut();
                    NavHostFragment.findNavController(this).navigate(R.id.action_gameDescription_to_login);
                    break;
                }
                case R.id.requests_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_gameDescription_to_requests);
                    break;
                }
                case R.id.favorites_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_gameDescription_to_favoritesGames);
                    break;
                }
                case R.id.friends_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_gameDescription_to_friends2);
                    break;
                }
                case R.id.search_users:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_gameDescription_to_filterFriends);
                    break;
                }
                case R.id.other_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_gameDescription_to_category);
                    break;
                }
            }
        }
        return true;
    }
}