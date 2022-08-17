This is not an officially supported Google product. It is not supported by Google and Google specifically disclaims all warranties as to its quality, merchantability, or fitness for a particular purpose.

# Google-chat-notification

This project provide tools that helps to send message to Google Chat from any pub/sub message. The tools support templating messages base on [Handlebar](https://github.com/jknack/handlebars.java)

## Create a Google chat webhook

To create a webhook, register it in the Google Chat space you want to receive messages, then write a script that sends messages.

### Step 1: Register the incoming webhook

  1. Open [Google Chat](https://chat.google.com/) in a web browser.
  1. Go to the space to which you want to add a webhook.
  1. At the top, next to space title, click <span class="material-icons">arrow_drop_down</span> Down Arrow > <img src="https://fonts.gstatic.com/s/i/short-term/release/googlesymbols/settings/default/24px.svg" class="inline-icon" alt="The icon for manage webhooks"> **Manage webhooks**.
  1. If this space already has other webhooks, click **Add another**. Otherwise, skip this step.
  1. For **Name**, enter "Quickstart Webhook".
  1. For **Avatar URL**, enter `https://developers.google.com/chat/images/chat-product-icon.png`.
  1. Click **SAVE**.
  1. Click <span class="material-icons">content_copy</span> Copy to copy the full webhook URL.
  1. Click outside the box to close the Incoming webhooks dialog.


For more details, please refer to this [documentation](https://developers.google.com/chat/how-tos/webhooks#create_a_webhook)

## Deploy a handlebar (HBS) template and upload to a GCS bucket

This function use handlebar templating. Thanks to this you can create powerfull message template depends on the pub/sub message recieved. Google chat support 2 type of message : 

* [Card](https://developers.google.com/chat/api/guides/message-formats/cards)
* [Message](https://developers.google.com/chat/api/guides/message-formats/basic)

You can find 2 sample of Handlebars templating for message [here](src/test/resources/message.hbs) with corresponding [event](src/test/resources/input_02.json) and for cards [here](src/test/resources/template_01.hbs) with this [event](src/test/resources/input_01.json).

To render the .hbs template, the library use JsonPath. You receive a JSON message from pubsub, then the function populate the template with the JSON value refered to the JsonPath syntax provided in your hbs file ( with the {{ <"jsonptath query">}} syntax).


Let say your hbs file is "mytemplate.hbs" and the bucket "mybucket-hbs" has been created. Execute : 


```bash
# if my mybucket-hbs does not exist
# gsutil mb gs://mybucket-hbs/

# Copy template to the target bucket
gsutil cp mytemplate.hbs gs://mybucket-hbs/
```


## Deploy the Cloud Function

For more details about how to deploy a Cloud Function, please refer to this [documentation](https://cloud.google.com/functions/docs/deploy#basics)

### Clone the github repo

1. First of all clone the github repo


```bash
git clone https://github.com/jbleroy1/google-chat-notifier.git
```
2. Create pub/sub topic
 

 ```bash
 gcloud pubsub topics create chat-notification
 ```

3. Deploy Cloud Function

 ```bash
 gcloud functions deploy my-pubsub-function \
  --gen2 \
  --region=europe-west2 \
  --runtime=java17 \
  --source=./google-chat-notifier \
  --entry-point=com.google.cloud.PubSubListener \
  --trigger-topic=chat-notification \
  --set-env-vars CHAT_URL="< chat web hook >",TEMPLATE_FILE_BUCKET=< bucket name >,TEMPLATE_FILE_OBJECT=< HBS object name >
 ```


## Service account

This documentation use the default service account provided by Cloud Function. You can specify your own SA. Be sure this SA have : 

* Read access to the HBS GCS bucket (access to the template)
* Pub/Sub can send notification to your Cloud Function endpoint




