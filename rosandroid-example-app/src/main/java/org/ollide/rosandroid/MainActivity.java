/*
 * Copyright (C) 2014 Oliver Degener.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ollide.rosandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ros.RosCore;
import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.master.uri.StaticMasterUriProvider;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {

    RosCore rosCore = null;
    java.net.URI coreUri = null;

    TextView msgView = null;
    EditText editText = null;
    SimplePublisherNode nodePub = null;

    public MainActivity() {
        super("RosAndroidExample", "RosAndroidExample");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msgView = (TextView)findViewById(R.id.message_view);
        editText = (EditText)findViewById(R.id.edit_send_message);

    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
  //      initCore();

        String uniqueIp = InetAddressFactory.newNonLoopback().getHostAddress().toString();
        uniqueIp = uniqueIp.replace(".", "_");
        nodePub = new SimplePublisherNode(uniqueIp);

        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(nodePub, nodeConfiguration);



        NodeMain nodeSub = new SimpleSubscriberNode(this, uniqueIp);

        NodeConfiguration subConfiguration = NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(nodeSub, nodeConfiguration);

        showMessage(getMasterUri().toASCIIString());
    }


    private void initCore() {
        if(rosCore == null) {
            rosCore = RosCore.newPublic();
            coreUri = rosCore.getUri();
            Log.i("RosActivity", "Core started with Uri: " + coreUri.getHost());
        }
    }

    public void showMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgView.append(msg + "\n");
            }
        });
    }

    public void onSendMessage(View v) {
        String s = editText.getText().toString();
        nodePub.sendPubMessage(s);
    }

    public void onClearMessages(View v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgView.setText(getMasterUri().toASCIIString() + "\n");
            }
        });

    }


}