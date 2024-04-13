package com.example.android1finalproject.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.android1finalproject.R;
import com.example.android1finalproject.adapter.MainScreenAdapter;
import com.example.android1finalproject.adapter.UserListAdapter;
import com.example.android1finalproject.models.Games;
import com.example.android1finalproject.models.Likes;
import com.example.android1finalproject.models.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.annotations.concurrent.Background;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserList extends Fragment implements UserListAdapter.OnUserClickListener,UserListAdapter.OnUserLongClickListener{
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private UserListAdapter adapter;
    private ArrayList<Likes> likedUsers;
    private FirebaseAuth auth;
    private FirebaseFirestore database;
    public static String gameName;
    String currentUserName;
    AlertDialog.Builder builder;
    public static HashMap<String,Object> friendList;
    public static HashMap<String,Object> userSenderList;




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatForGame.
     */
    // TODO: Rename and change types and number of parameters
    public static UserList newInstance(String param1, String param2) {
        UserList fragment = new UserList();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat_for_game, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_chat_for_game);
        currentUserName = " ";
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        likedUsers = new ArrayList<>();
        friendList = new HashMap<>();
        userSenderList = new HashMap<>();
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        Toast.makeText(requireContext(), R.string.user_list_start, Toast.LENGTH_SHORT).show();



        database.collection("games").document(gameName).collection("Likes")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        likedUsers.clear();
                        for (DocumentSnapshot doc :queryDocumentSnapshots.getDocuments())
                        {

                            if(!Objects.equals(auth.getUid(), doc.getString("userId")))
                            {
                                Likes username= new Likes(doc.getString("username"),doc.getString("userId")) ;
                                likedUsers.add(username);
                            }
                            else {
                                currentUserName = doc.getString("username");
                            }

                        }
                        database.collection("games").document(gameName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Glide.with(UserList.this)
                                        .load(documentSnapshot.getString("image"))
                                        .into(new SimpleTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {
                                                recyclerView.setBackground(resource);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        });




                    }
                });
        builder = new AlertDialog.Builder(this.getContext());
        adapter = new UserListAdapter(likedUsers);
        adapter.setClickListener(this);
        adapter.setLongClickListener(this);
        recyclerView.setAdapter(adapter);
        return v;
    }


    @Override
    public void onUserClick(int position) {
        Likes user = likedUsers.get(position);
        Chat.gamename = gameName;
        Chat.friendId = user.getUserId();
        Chat.reciverName = user.getUsername();
        Chat.senderName = currentUserName;
        Chat.lastFragmentTag = "UserList";
        NavHostFragment.findNavController(this).navigate(R.id.action_chatForGame_to_chat);
    }

    @Override
    public void onUserLongClicked(int position) {
        Likes user = likedUsers.get(position);
        String friendId = user.getUserId();
        DocumentReference friendRef = database.collection("users").document(friendId);
        DocumentReference addFriendRef = database.collection("users").document(auth.getUid());
        DocumentReference userRef = addFriendRef.collection("requests").document(friendId);
        DocumentReference userRef2 = friendRef.collection("requests").document(auth.getUid());

        friendRef.collection("requests").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<String> uidList = new ArrayList<>();
                for(DocumentSnapshot doc:queryDocumentSnapshots.getDocuments())
                {
                    uidList.add(doc.getString("userId"));
                }
                if(uidList.contains(auth.getUid()))
                {
                    Toast.makeText(requireContext(), R.string.there_is_a_request, Toast.LENGTH_SHORT).show();
                }
                else {
                    friendRef.collection("friends").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots1) {
                            ArrayList<String> uidList = new ArrayList<>();
                            for(DocumentSnapshot doc1:queryDocumentSnapshots1.getDocuments())
                            {
                                uidList.add(doc1.getString("userId"));
                            }
                            if(uidList.contains(auth.getUid()))
                            {
                                Toast.makeText(requireContext(), R.string.you_are_friends, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                builder.setTitle(R.string.add_friend).setMessage(getString(R.string.Would_you_like_to_add) +
                                        user.getUsername() +
                                        getString(R.string.to_your_friends)).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        userRef.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> doc) {
                                                        friendRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                DocumentSnapshot friendResult = task.getResult();
                                                                String username = friendResult.getString("username");
                                                                String status = "for you";
                                                                String userId = auth.getUid();
                                                                Request request = new Request(username,status,userId);
                                                                friendList.put("username", friendResult.getString("username"));
                                                                friendList.put("status", "waiting to response");
                                                                friendList.put("userId", friendId);
                                                                userRef.set(request);
                                                                // Perform first set operation
                                                                userRef
                                                                        .set(friendList)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                // Success handling for first set operation
                                                                                // After the first set operation is successful, perform the second set operation

                                                                                userSenderList.put("username", currentUserName);
                                                                                userSenderList.put("status", "for you");
                                                                                userSenderList.put("userId", auth.getUid());
                                                                                Requests.friendId = friendId;

                                                                                userRef2
                                                                                        .set(userSenderList)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void unused) {
                                                                                                userRef2.set(userSenderList);
                                                                                                userRef.set(friendList);
                                                                                            }
                                                                                        });
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                    }
                                                });

                                    }

                                }).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
        menu.findItem(R.id.other_action).setTitle(R.string.main_screen);
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
                    NavHostFragment.findNavController(this).navigate(R.id.action_chatForGame_to_login);
                    break;
                }
                case R.id.requests_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_chatForGame_to_requests);
                    break;
                }
                case R.id.favorites_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_chatForGame_to_favoritesGames);
                    break;
                }
                case R.id.friends_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_chatForGame_to_friends2);
                    break;
                }
                case R.id.search_users:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_chatForGame_to_filterFriends);
                    break;
                }
                case R.id.other_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_chatForGame_to_category);
                    break;
                }
            }
        }
        return true;
    }
}