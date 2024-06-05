package com.example.apiretrofit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // UI elements
    private TextView textView;
    private Button fetchAllButton, fetchSingleButton, postButton, patchButton, deleteButton;
    private EditText idInput, firstNameInput, lastNameInput, addressInput, rollNumberInput, mobileInput;

    // Base URL for the API
    private static final String BASE_URL = "https://6bf0-98-97-42-40.ngrok-free.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        textView = findViewById(R.id.textView);
        fetchAllButton = findViewById(R.id.fetchAllButton);
        fetchSingleButton = findViewById(R.id.fetchSingleButton);
        postButton = findViewById(R.id.postButton);
        patchButton = findViewById(R.id.patchButton);
        deleteButton = findViewById(R.id.deleteButton);
        idInput = findViewById(R.id.idInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        addressInput = findViewById(R.id.addressInput);
        rollNumberInput = findViewById(R.id.rollNumberInput);
        mobileInput = findViewById(R.id.mobileInput);

        // Set up Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of the API service
        ApiService apiService = retrofit.create(ApiService.class);

        // Set up listeners for buttons
        fetchAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch all data from the API
                new GetApiDataTask().execute(BASE_URL + "api/basic/?format=json");
            }
        });

        fetchSingleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch a single data entry by ID
                int id = Integer.parseInt(idInput.getText().toString());
                new GetSingleApiDataTask().execute(BASE_URL + "api/basic/" + id + "/?format=json");
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new API response object and set its fields from the input fields
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String address = addressInput.getText().toString();
                int rollNumber = Integer.parseInt(rollNumberInput.getText().toString());
                String mobile = mobileInput.getText().toString();

                ApiResponse newApiResponse = new ApiResponse();
                newApiResponse.setFirstName(firstName);
                newApiResponse.setLastName(lastName);
                newApiResponse.setAddress(address);
                newApiResponse.setRollNumber(rollNumber);
                newApiResponse.setMobile(mobile);

                // Send a POST request to the API to create the new entry
                Call<ApiResponse> call = apiService.postApiResponse(newApiResponse);
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Display the response from the server
                            textView.setText("Post Response was successful");
                        } else {
                            // Indicate that the POST request was not successful
                            textView.setText("Post Response was not successful");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        // Display an error message
                        textView.setText("Error: " + t.getMessage());
                    }
                });
            }
        });

        patchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update an existing entry in the API
                int id = Integer.parseInt(idInput.getText().toString());
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String address = addressInput.getText().toString();
                int rollNumber = Integer.parseInt(rollNumberInput.getText().toString());
                String mobile = mobileInput.getText().toString();

                ApiResponse updatedApiResponse = new ApiResponse();
                updatedApiResponse.setFirstName(firstName);
                updatedApiResponse.setLastName(lastName);
                updatedApiResponse.setAddress(address);
                updatedApiResponse.setRollNumber(rollNumber);
                updatedApiResponse.setMobile(mobile);

                // Send a PATCH request to update the entry
                new PatchApiDataTask().execute(BASE_URL + "api/basic/" + id + "/", new Gson().toJson(updatedApiResponse));
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete an entry by ID
                int id = Integer.parseInt(idInput.getText().toString());
                Call<Void> call = apiService.deleteApiResponse(id);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Indicate that the entry was deleted successfully
                            textView.setText("Record deleted successfully");
                        } else {
                            // Indicate that the delete request was not successful
                            textView.setText("Delete Response was not successful");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Display an error message
                        textView.setText("Error: " + t.getMessage());
                    }
                });
            }
        });
    }

    // AsyncTask to fetch all data from the API
    private class GetApiDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String urlString = strings[0];
            StringBuilder result = new StringBuilder();
            try {
                // Create a URL object from the URL string
                URL url = new URL(urlString);
                // Open a connection to the URL
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // Set the request method to GET
                urlConnection.setRequestMethod("GET");
                // Create a BufferedReader to read the response from the server
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                // Read the response line by line and append it to the result
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();
                // Disconnect the URL connection
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Return the result as a string
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // Display the result in the textView
            textView.setText(result);
        }
    }

    // AsyncTask to fetch a single data entry from the API by ID
    private class GetSingleApiDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String urlString = strings[0];
            StringBuilder result = new StringBuilder();
            try {
                // Create a URL object from the URL string
                URL url = new URL(urlString);
                // Open a connection to the URL
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // Set the request method to GET
                urlConnection.setRequestMethod("GET");
                // Create a BufferedReader to read the response from the server
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                // Read the response line by line and append it to the result
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();
                // Disconnect the URL connection
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Return the result as a string
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // Display the result in the textView
            textView.setText(result);
        }
    }

    // AsyncTask to patch (update) an existing data entry in the API
    private class PatchApiDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String urlString = strings[0];
            String jsonInputString = strings[1];
            StringBuilder result = new StringBuilder();
            try {
                // Create a URL object from the URL string
                URL url = new URL(urlString);
                // Open a connection to the URL
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // Set the request method to PATCH
                urlConnection.setRequestMethod("PATCH");
                // Set the request property to indicate the content type is JSON
                urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                // Allow output to the connection
                urlConnection.setDoOutput(true);
                // Write the JSON input string to the output stream
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"))) {
                    writer.write(jsonInputString);
                    writer.flush();
                }
                // Create a BufferedReader to read the response from the server
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                // Read the response line by line and append it to the result
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();
                // Disconnect the URL connection
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Return the result as a string
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            // Display the result in the textView
            textView.setText(result);
        }
    }
}