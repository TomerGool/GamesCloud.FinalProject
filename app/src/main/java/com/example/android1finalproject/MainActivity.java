package com.example.android1finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.android1finalproject.models.Games;
import com.example.android1finalproject.models.User;
import com.example.android1finalproject.ui.MainScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore;
    public Boolean isSuccessfulLogin = false;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    public void createUser(String email,String password,String phoneNumber, String userName)
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addData(email,phoneNumber,userName);
                            Toast.makeText(MainActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.unsuccessful, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void addGame(String name, String added , ArrayList<String> genres , ArrayList<String> platform , String image)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> game = new HashMap<>();
        Games newGame = new Games(name,added,genres , platform , image);
        DocumentReference documentReference = db.collection("games").document(name);
        documentReference.set(newGame);
        game.put("name",name);
        game.put("added", added);
        game.put("genres", genres);
        game.put("platform", platform);
        game.put("image", image);
        documentReference.set(game).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, getString(R.string.the_game) + name + getString(R.string.added_to_database), Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,getString(R.string.the_game) + name + getString(R.string.is_not_added_to_database), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void addData(String email,String phoneNumber, String userName)
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();
        User newUser = new User(email,phoneNumber,userName);
        DocumentReference documentReference = db.collection("users").document(userId);
        documentReference.set(newUser);
        user.put("username",userName);
        user.put("email",email);
        user.put("phone",phoneNumber);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, R.string.welcome_to_database, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}