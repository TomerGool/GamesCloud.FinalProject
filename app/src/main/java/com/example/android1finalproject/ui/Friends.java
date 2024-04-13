package com.example.android1finalproject.ui;

import android.app.AlertDialog;
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
import com.example.android1finalproject.models.Friend;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Friends#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Friends extends Fragment implements FriendsAdapter.OnUserClickListener,FriendsAdapter.OnUserLongClickListener{
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private FriendsAdapter adapter;
    public static ArrayList<Friend> friendsArr;
    private FirebaseFirestore database;
    private FirebaseAuth auth;
    private AlertDialog.Builder builder;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Friends() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Friends.
     */
    // TODO: Rename and change types and number of parameters
    public static Friends newInstance(String param1, String param2) {
        Friends fragment = new Friends();
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
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsArr = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.friends_rec);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Toast.makeText(requireContext(), getString(R.string.friends_start), Toast.LENGTH_SHORT).show();
        database.collection("users").document(auth.getUid()).collection("friends").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot doc : docs) {
                            String name = doc.getString("name");
                            String email = doc.getString("email");
                            String phone = doc.getString("phoneNumber");
                            friendsArr.add(new Friend(name, doc.getId(),email,phone));
                        }

                        // Create and set the adapter after data retrieval is complete
                        adapter = new FriendsAdapter(friendsArr);
                        adapter.setClickListener(Friends.this);
                        adapter.setLongClickListener(Friends.this);
                        recyclerView.setAdapter(adapter);
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
                        itemTouchHelper.attachToRecyclerView(recyclerView);
                    }
                });
        builder  = new AlertDialog.Builder(this.getContext());

        return v;
    }
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(
            0, // no drag-and-drop
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            DocumentReference userDeleteFriend = database.collection("users").document(auth.getUid())

                    .collection("friends").document(friendsArr.get(position).getUserId());
            DocumentReference friendDeleteUser = database.collection("users").document(friendsArr.get(position).getUserId())
                    .collection("friends").document(auth.getUid());
            friendsArr.remove(position);
            deleteFriend(userDeleteFriend,friendDeleteUser);
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.getAdapter().notifyItemRemoved(position);
        }


    };


    @Override
    public void onUserClick(int position) {
        database.collection("users").document(auth.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Friend user = friendsArr.get(position);
                Chat.friendId = user.getUserId();
                Chat.reciverName = user.getName();
                Chat.senderName = documentSnapshot.getString("username");
                Chat.lastFragmentTag = "Friends";
                Toast.makeText(requireContext(), R.string.welcome_to_chat, Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(Friends.this).navigate(R.id.action_friends2_to_chat);
            }
        });

    }
    public static void deleteFriend(DocumentReference userDeleteFriend, DocumentReference friendDeleteUser)
    {
        userDeleteFriend.delete();
        friendDeleteUser.delete();
    }

    @Override
    public void OnUserLongClick(int position) {
        Friend user = friendsArr.get(position);

        builder.setTitle(R.string.your_friend_details)
                .setMessage(R.string.username + user.getName() + "\n"+
                        getString(R.string.userid) + user.getUserId() + "\n"+
                        R.string.email+ user.getEmail()+"\n"+
                        R.string.phone_number+ user.getPhoneNumber())
                .show();
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
        menu.findItem(R.id.other_action).setVisible(false);
        menu.findItem(R.id.friends_action).setTitle(getString(R.string.main_screen));
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
                    NavHostFragment.findNavController(this).navigate(R.id.action_friends2_to_login);
                    break;
                }
                case R.id.requests_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_friends2_to_requests);
                    break;
                }
                case R.id.favorites_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_friends2_to_favoritesGames);
                    break;
                }
                case R.id.friends_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_friends2_to_category);
                    break;
                }
                case R.id.search_users:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_friends2_to_filterFriends);
                    break;
                }
            }
        }
        return true;
    }
}