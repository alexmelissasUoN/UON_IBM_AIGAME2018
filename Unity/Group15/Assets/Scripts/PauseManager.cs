﻿using UnityEngine;
using UnityEngine.SceneManagement;
using VoxelBusters.NativePlugins;

public class PauseManager : MonoBehaviour {

    private bool start = true;

    private void Awake()
    {
        ZPlayerPrefs.Initialize("small fluffy puppies", "I'mASaltySalter");
    }

    public void Save()
    {
        if (UserSession.us.user != null && UserSession.us.user.getID() != "")
        {
            ZPlayerPrefs.SetString("id", UserSession.us.user.getID());
            ZPlayerPrefs.Save();
        }
    }

    public void Load()
    {
        if (!start)
        {
            if (PlayerPrefs.HasKey("id") && ZPlayerPrefs.GetRowString("id") != "")
            {
                NPBinding.UI.ShowToast("id: "+ZPlayerPrefs.GetString("id"), eToastMessageLength.SHORT);
                string scene = SceneManager.GetActiveScene().name;
                if (scene == "StartScreen" || scene == "Start_Login" || scene == "TwitterLogin" || scene == "ModelCreated" || scene == "CharacterCreation" || scene == "CreateAccount") //need other solution for settings
                    gameObject.AddComponent<UpdateSessions>().U_User();
                else
                    gameObject.AddComponent<UpdateSessions>().U_All();
            }                          
        }         
    }

    void OnApplicationPause(bool pauseStatus)
    {
        if (pauseStatus)
        {
            start = false;
            Save();
        }
        else Load();
    }
}