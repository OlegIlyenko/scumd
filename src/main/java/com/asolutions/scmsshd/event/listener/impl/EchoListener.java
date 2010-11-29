package com.asolutions.scmsshd.event.listener.impl;

import com.asolutions.scmsshd.event.Post;
import com.asolutions.scmsshd.event.PushEvent;
import com.asolutions.scmsshd.util.GitUtil;

/**
 * @author Oleg Ilyenko
 */
public class EchoListener {

//    public void echoPost(@Post Event event) {
//        System.err.println("-----> [POST EVENT] " + event);
//    }
//
//    public void echoPre(@Pre Event event) {
//        System.err.println("-----> [PRE EVENT] " + event);
//    }
//
    public void echoPre(@Post PushEvent event) {
        System.out.println(GitUtil.render(event));
    }

}
