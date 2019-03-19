﻿using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

//! View username on the overworld
public class ViewUserOverworld : MonoBehaviour {

    public Text usernameLabel;

	void Update () {
        if (UserSession.us != null && UserSession.us.user.GetUsername()!="")
            usernameLabel.GetComponentInChildren<Text>().text = UserSession.us.user.GetUsername();
        else
            usernameLabel.GetComponentInChildren<Text>().text = "<Invalid Session>";
    }
}
