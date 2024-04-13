package com.example.android1finalproject.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android1finalproject.R;
import com.example.android1finalproject.adapter.FriendsAdapter;
import com.example.android1finalproject.adapter.MainScreenAdapter;
import com.example.android1finalproject.adapter.RequestsAdapter;
import com.example.android1finalproject.adapter.UserListAdapter;
import com.example.android1finalproject.models.Friend;
import com.example.android1finalproject.models.Games;
import com.example.android1finalproject.models.Likes;
import com.example.android1finalproject.models.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Requests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Requests extends Fragment implements RequestsAdapter.OnUserClickListener {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RequestsAdapter adapter;
    public static ArrayList<Request> requestArr;
    private FirebaseAuth auth;
    AlertDialog.Builder builder;
    private  FirebaseFirestore database;
    public static String friendId;

    public static String userId;
    public static String friendName;
    public static String username;
    static String yourPhone;
    static String yourEmail;
    static String friendPhone;
    static String friendEmail;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Requests() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Requests.
     */
    // TODO: Rename and change types and number of parameters
    public static Requests newInstance(String param1, String param2) {
        Requests fragment = new Requests();
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
        View v = inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.requests_rec);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        requestArr = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        Toast.makeText(requireContext(), R.string.requests_start, Toast.LENGTH_SHORT).show();


        database.collection("users").document(auth.getUid()).collection("requests").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot doc : docs) {
                            String name = doc.getString("username");
                            String status = doc.getString("status");

                            requestArr.add(new Request(name,status, doc.getId()));
                        }

                        // Create and set the adapter after data retrieval is complete
                        adapter = new RequestsAdapter(requestArr);
                        adapter.setClickListener(Requests.this);
                        recyclerView.setAdapter(adapter);
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
                            itemTouchHelper.attachToRecyclerView(recyclerView);
                    }
                });
        builder = new AlertDialog.Builder(this.getContext());

        return v;
    }
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(
            0, // no drag-and-drop
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) { // allow swipe both directions

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            DocumentReference userRequestFriend = database.collection("users").document(auth.getUid())

                    .collection("requests").document(requestArr.get(position).getUserId());
            DocumentReference friendRequestUser = database.collection("users").document(requestArr.get(position).getUserId())
                    .collection("requests").document(auth.getUid());
            requestArr.remove(position);
            deleteRequest(userRequestFriend,friendRequestUser);
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.getAdapter().notifyItemRemoved(position);
        }


    };

    @Override
    public void onUserClick(int position) {
        Request user = requestArr.get(position);
        friendId = user.getUserId();
        friendName = user.getName();


        if(user.getStatus().equals("for you") )
        {
            DocumentReference userRequestFriend = database.collection("users").document(auth.getUid())
                .collection("requests").document(friendId);
            DocumentReference friendRequestUser = database.collection("users").document(friendId)
                    .collection("requests").document(auth.getUid());

            friendRequestUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot doc = task.getResult();
                    username = doc.getString("username");
                }
            });

            builder.setTitle(R.string.add_friend).setMessage(getString(R.string.tap_yes_if_you_want_to_add)
                            + user.getName()+getString(R.string.to_your_friends_tap_no_if_you_want_to_remove_this_request))
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            addFriend(auth.getCurrentUser().getUid(),friendId);
                            deleteRequest(userRequestFriend,friendRequestUser);
                        }
                    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            deleteRequest(userRequestFriend,friendRequestUser);
                        }
                    }).show();

        }
        else {
            Toast.makeText(requireContext(), getString(R.string.we_wait_to)+ user.getName()+ getString(R.string.confirm), Toast.LENGTH_SHORT).show();
        }

    }
    public static void addFriend(String userId, String _friendId)
    {
        FirebaseFirestore _database = FirebaseFirestore.getInstance();
        CollectionReference userAddFriend = _database.collection("users").document(userId)
                .collection("friends");
        CollectionReference friendAddUser = _database.collection("users").document(_friendId)
                .collection("friends");


        _database.collection("users").document(_friendId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                friendPhone = doc.getString("phone");
                friendEmail = doc.getString("email");
                friendName = doc.getString("username");
                userAddFriend.document(_friendId).set(
                        new Friend(friendName,_friendId,friendEmail,friendPhone));
            }
        });
        _database.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                yourPhone = doc.getString("phone");
                yourEmail = doc.getString("email");
                username = doc.getString("username");
                friendAddUser.document(userId).set(new Friend(
                        username, userId,yourEmail,yourPhone
                ));
            }
        });
    }
    public static void deleteRequest(DocumentReference userRequestFriend,DocumentReference friendRequestUser)
    {
        userRequestFriend.delete();
        friendRequestUser.delete();
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
        menu.findItem(R.id.other_action).setVisible(false);
        menu.findItem(R.id.requests_action).setTitle(getString(R.string.main_screen));
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
                    NavHostFragment.findNavController(this).navigate(R.id.action_requests_to_login);
                    break;
                }
                case R.id.requests_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_requests_to_category);
                    break;
                }
                case R.id.favorites_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_requests_to_favoritesGames);
                    break;
                }
                case R.id.friends_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_requests_to_friends2);
                    break;
                }
                case R.id.search_users:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_requests_to_filterFriends);
                    break;
                }
            }
        }
        return true;
    }
}