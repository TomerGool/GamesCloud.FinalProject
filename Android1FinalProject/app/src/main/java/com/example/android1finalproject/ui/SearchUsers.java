package com.example.android1finalproject.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.android1finalproject.R;
import com.example.android1finalproject.adapter.RequestsAdapter;
import com.example.android1finalproject.adapter.SearchUsersAdapter;
import com.example.android1finalproject.adapter.UserListAdapter;
import com.example.android1finalproject.models.Friend;
import com.example.android1finalproject.models.Request;
import com.example.android1finalproject.models.SearchUsersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchUsers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchUsers extends Fragment implements SearchUsersAdapter.OnUserClickListener,SearchUsersAdapter.OnUserLongClickListener {

    private ArrayList<SearchUsersModel> userList;
    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SearchUsersAdapter adapter;
    AlertDialog.Builder builder;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchUsers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterFriends.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchUsers newInstance(String param1, String param2) {
        SearchUsers fragment = new SearchUsers();
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
        View v = inflater.inflate(R.layout.fragment_serarch_users, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.search_rec);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        Toast.makeText(requireContext(), R.string.search_users_start, Toast.LENGTH_SHORT).show();

        CollectionReference getUserId = database.collection("users");


        getUserId.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                {
                    ArrayList<String> favoriteGames = new ArrayList<>();
                    ArrayList<String> genres = new ArrayList<>();
                    ArrayList<String> platforms = new ArrayList<>();
                    SearchUsersModel forUser = new SearchUsersModel(doc.getString("username"),doc.getId()
                            ,doc.getString("email"),doc.getString("phone"));
                    getUserId.document(doc.getId()).collection("Likes").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots1) {
                            for(DocumentSnapshot doc1 : queryDocumentSnapshots1.getDocuments())
                            {
                                favoriteGames.add(doc1.getString("game"));
                                ArrayList<String> arr = (ArrayList<String>) doc1.get("genres");
                                for(int i=0;i< arr.size();i++)
                                {
                                    if(!genres.contains(arr.get(i)))
                                    {
                                        genres.add(arr.get(i));
                                    }
                                }
                                arr = (ArrayList<String>) doc1.get("platform");
                                for(int i=0;i<arr.size();i++)
                                {
                                    if(!platforms.contains(arr.get(i)))
                                    {
                                        platforms.add(arr.get(i));
                                    }
                                }
                            }
                            SearchUsersModel forGame = new SearchUsersModel(favoriteGames,genres,platforms);
                            userList.add(new SearchUsersModel(forUser,forGame));
                            adapter = new SearchUsersAdapter(userList);
                            adapter.setClickListener(SearchUsers.this);
                            adapter.setLongClickListener(SearchUsers.this);
                            recyclerView.setAdapter(adapter);
                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
                            itemTouchHelper.attachToRecyclerView(recyclerView);

                        }
                    });
                }


            }

        });
        SearchView searchBtn = (SearchView) v.findViewById(R.id.search_input);
        searchBtn.clearFocus();
        searchBtn.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });


        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_username:
                        adapter.setCategoryRb("username");
                        break;
                    case R.id.rb_email:
                        adapter.setCategoryRb("email");
                        break;
                    case R.id.rb_phone:
                        adapter.setCategoryRb("phone");
                        break;
                    case R.id.rb_favorite_games:
                        adapter.setCategoryRb("favorite games");
                        break;
                    case R.id.rb_genres:
                        adapter.setCategoryRb("genres");
                        break;
                    case R.id.rb_platforms:
                        adapter.setCategoryRb("platforms");

                }
            }
        });
        builder  = new AlertDialog.Builder(this.getContext());
        return v;
    }


    @Override
    public void onUserClick(int position) {
        SearchUsersModel user = userList.get(position);

        if (user.getForUser().getUserId().equals(auth.getUid())) {
            Toast.makeText(requireContext(), R.string.it_is_you, Toast.LENGTH_SHORT).show();
        }
        else
        {
            database.collection("users").document(auth.getUid()).collection("requests")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            DocumentReference userRequestFriend = database.collection("users").document(auth.getUid())
                                    .collection("requests").document(user.getForUser().getUserId());
                            DocumentReference friendRequestUser = database.collection("users").document(user.getForUser().getUserId())
                                    .collection("requests").document(auth.getUid());
                            for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                            {
                                if(doc.getString("userId").equals(user.getForUser().getUserId()))
                                {
                                    if(doc.getString("status").equals("for you"))
                                    {

                                        builder.setTitle(R.string.add_friend).setMessage(R.string.tap_yes_if_you_want_to_add
                                                        + user.getForUser().getUsername()+R.string.to_your_friends_tap_no_if_you_want_to_remove_this_request)
                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Requests.friendId = user.getForUser().getUserId();
                                                        Requests.userId = auth.getCurrentUser().getUid();
                                                        Requests.addFriend(user.getForUser().getUserId(),auth.getCurrentUser().getUid());
                                                        Requests.deleteRequest(userRequestFriend,friendRequestUser);
                                                    }
                                                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Requests.deleteRequest(userRequestFriend,friendRequestUser);
                                                    }
                                                }).show();
                                        Toast.makeText(requireContext(), R.string.please_conpirm, Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    else{
                                        Toast.makeText(requireContext(), R.string.we_wait_to+user.getForUser().getUsername()+R.string.confirm, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), R.string.you_have_no_requests, Toast.LENGTH_SHORT).show();
                        }
                    });

            database.collection("users").document(auth.getUid()).collection("friends")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                            {
                                if(doc.getString("userId").equals(user.getForUser().getUserId()))
                                {
                                    builder.setTitle(R.string.your_friend).setMessage(getString(R.string.would_you_like_to_open_chat_with) + user.getForUser().getUsername() + "?")
                                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Toast.makeText(requireContext(), R.string.welcome_to_chat, Toast.LENGTH_SHORT).show();
                                                    Chat.lastFragmentTag = "SearchUsers";
                                                    Chat.friendId = user.getForUser().getUserId();
                                                    NavHostFragment.findNavController(SearchUsers.this).navigate(R.id.action_filterFriends_to_chat);
                                                }
                                            }).show();

                                    break;
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), R.string.you_have_no_friends, Toast.LENGTH_SHORT).show();
                        }
                    });

            database.collection("users").document(auth.getUid()).collection("friends").
                    get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            ArrayList<String> friendsIdArr = new ArrayList<>();
                            for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                            {
                                friendsIdArr.add(doc.getString("userId"));
                            }
                            if(!friendsIdArr.contains(user.getForUser().getUserId()))
                            {
                                database.collection("users").document(auth.getUid()).collection("requests")
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots1) {
                                                ArrayList<String> requestsIdArr = new ArrayList<>();
                                                for(DocumentSnapshot doc1 : queryDocumentSnapshots1.getDocuments())
                                                {
                                                    requestsIdArr.add(doc1.getId());
                                                }
                                                if(!requestsIdArr.contains(user.getForUser().getUserId()))
                                                {

                                                    builder.setTitle(getString(R.string.send_request_to) + user.getForUser().getUsername())
                                                            .setMessage(getString(R.string.would_you_like_to_send_a_friend_request_to) + user.getForUser().getUsername() + "?")
                                                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    DocumentReference friendRef = database.collection("users").document(user.getForUser().getUserId());
                                                                    DocumentReference addFriendRef = database.collection("users").document(auth.getUid());
                                                                    DocumentReference userRef = addFriendRef.collection("requests").document(user.getForUser().getUserId());
                                                                    DocumentReference userRef2 = friendRef.collection("requests").document(auth.getUid());
                                                                    String status = "for you";
                                                                    UserList.friendList = new HashMap<>();
                                                                    UserList.userSenderList = new HashMap<>();
                                                                    UserList.friendList.put("username",user.getForUser().getUsername());
                                                                    UserList.friendList.put("status","waiting to response");
                                                                    UserList.friendList.put("userId",user.getForUser().getUserId());
                                                                    userRef.set(UserList.friendList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            database.collection("users").document(auth.getUid()).get()
                                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                        @Override
                                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                            UserList.userSenderList.put("username",documentSnapshot.getString("username"));
                                                                                            UserList.userSenderList.put("status",status);
                                                                                            UserList.userSenderList.put("userId",auth.getUid());
                                                                                            Requests.friendId = user.getForUser().getUserId();
                                                                                            userRef2.set(UserList.userSenderList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    userRef.set(UserList.friendList);
                                                                                                    userRef2.set(UserList.userSenderList);
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                                }
                                                            }).show();
                                                    Toast.makeText(requireContext(), R.string.send_him_a_request, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }
                        }
                    });
        }


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

            SearchUsersModel user = userList.get(position);
            for(Request request :Requests.requestArr)
            {
                if(user.getForUser().getUserId().equals(request.getUserId()))
                {
                    DocumentReference userRequestFriend = database.collection("users").document(auth.getUid())

                            .collection("requests").document(user.getForUser().getUserId());
                    DocumentReference friendRequestUser = database.collection("users").document(user.getForUser().getUserId())
                            .collection("requests").document(auth.getUid());
                    Requests.requestArr.remove(request);
                    Requests.deleteRequest(userRequestFriend,friendRequestUser);
                }
            }
            for(Friend friend :Friends.friendsArr)
            {
                if(user.getForUser().getUserId().equals(friend.getUserId()))
                {
                    DocumentReference userRequestFriend = database.collection("users").document(auth.getUid())

                            .collection("friends").document(user.getForUser().getUserId());
                    DocumentReference friendRequestUser = database.collection("users").document(user.getForUser().getUserId())
                            .collection("friends").document(auth.getUid());
                    Friends.friendsArr.remove(friend);
                    Friends.deleteFriend(userRequestFriend,friendRequestUser);
                }
            }



            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.getAdapter().notifyItemRemoved(position);
        }


    };
    @Override
    public void onUserLongClicked(int position) {
        SearchUsersModel user = userList.get(position);
        database.collection("users").document(auth.getUid()).collection("friends")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                {
                    if(doc.getString("userId").equals(user.getForUser().getUserId())
                            ||
                            user.getForUser().getUserId().equals(auth.getUid()))
                    {
                        builder.setTitle("your friend details")
                                .setMessage("username: " + user.getForUser().getUsername() + "\n"+
                                        "userId: " + user.getForUser().getUserId() + "\n"+
                                        "email: "+ user.getForUser().getEmail()+"\n"+
                                        "phone: "+user.getForUser().getPhone()+"\n"+
                                        "favorite games: "+user.getForGame().getFavoriteGames()+"\n"+
                                        "genres: "+user.getForGame().getGenres()+"\n"+
                                        "platforms: "+user.getForGame().getPlatforms())
                                .show();

                        break;
                    }
                }
            }
        });
    }
    private void filterList(String text)
    {
        ArrayList<SearchUsersModel> filteredList = new ArrayList<>();
        for(SearchUsersModel user :userList)
        {
            String username = user.getForUser().getUsername();
            String email = user.getForUser().getEmail();
            String phone = user.getForUser().getPhone();
            String favoriteGame = user.getForGame().getFavoriteGames().toString();
            String genre = user.getForGame().getGenres().toString();
            String platform = user.getForGame().getPlatforms().toString();
            if(adapter.getCategoryRb().equals("username"))
            {
                if(username != null && username.toLowerCase().contains(text.toLowerCase()))
                {
                    filteredList.add(user);
                }
            }
            if(adapter.getCategoryRb().equals("email"))
            {
                if(email != null && email.toLowerCase().contains(text.toLowerCase()))
                {
                    filteredList.add(user);
                }
            }
            if(adapter.getCategoryRb().equals("phone"))
            {
                if(phone != null && phone.toLowerCase().contains(text.toLowerCase()))
                {
                    filteredList.add(user);
                }
            }
            if(adapter.getCategoryRb().equals("favorite games"))
            {
                if(favoriteGame != null && favoriteGame.toLowerCase().contains(text.toLowerCase()))
                {
                    filteredList.add(user);
                }
            }
            if(adapter.getCategoryRb().equals("genres"))
            {
                if(genre != null && genre.toLowerCase().contains(text.toLowerCase()))
                {
                    filteredList.add(user);
                }
            }
            if(adapter.getCategoryRb().equals("platforms"))
            {
                if(platform != null && platform.toLowerCase().contains(text.toLowerCase()))
                {
                    filteredList.add(user);
                }
            }

        }
        if(filteredList.isEmpty())
        {
            Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show();
        }
        else {
            adapter.setFilteredList(filteredList);
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
        menu.findItem(R.id.other_action).setVisible(false);
        menu.findItem(R.id.search_users).setTitle("Main screen");
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
                    NavHostFragment.findNavController(this).navigate(R.id.action_filterFriends_to_login);
                    break;
                }
                case R.id.requests_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_filterFriends_to_requests);
                    break;
                }
                case R.id.favorites_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_filterFriends_to_favoritesGames);
                    break;
                }
                case R.id.friends_action:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_filterFriends_to_friends2);
                    break;
                }
                case R.id.search_users:
                {
                    NavHostFragment.findNavController(this).navigate(R.id.action_filterFriends_to_category);
                    break;
                }
            }
        }
        return true;
    }
}