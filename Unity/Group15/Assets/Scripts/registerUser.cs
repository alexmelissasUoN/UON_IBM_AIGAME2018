﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class registerUser : MonoBehaviour
{

    public InputField username_field;
    public InputField password_field;

    public void checkUserPass()
    {
        string username = username_field.text;
        string password = password_field.text;
        if (username == "" || password == "") 
        {
            Debug.Log("EMPTY");
            return;
        }
        if(username.Length>25)
        {
            Debug.Log("TOO LONG");
            return;
        }
        else
        {
            // request with post
        }

        Debug.Log(username + " " + password); //this would pass it to the server and check if available, and save
        if (true) // this would check if they're ok
            SceneManager.LoadScene("TwitterLogin");
        return;
    }
}