# Requires the installation of Tweepy library

import time
import json
from kafka import KafkaConsumer, KafkaProducer
from getpass import getpass
from textwrap import TextWrapper
from pymongo import MongoClient
import datetime as dt

import tweepy
from symbol import except_clause
from twitter_object import twitter_object
from _ast import If


class StreamWatcherListener(tweepy.StreamListener):

    status_wrapper = TextWrapper(width=30, initial_indent='    ', subsequent_indent='    ')
    db = None
    stop_condition = ""
    
    def publish_message(self, producer_instance, topic_name, key, value):
        try:
            key_bytes = bytes(key, encoding='utf-8')
            value_bytes = bytes(value, encoding='utf-8')
            producer_instance.send(topic_name, key=key_bytes, value=value_bytes)
            producer_instance.flush()
            print('Message published successfully.')
        except Exception as ex:
            print('Exception in publishing message')
            print(str(ex))


    def connect_kafka_producer(self):
        _producer = None
        try:
            _producer = KafkaProducer(bootstrap_servers=['localhost:9092'], api_version=(0, 10))
        except Exception as ex:
            print('Exception while connecting Kafka')
            print(str(ex))
        finally:
            return _producer
    
    def connect_mongoDB_Twitter(self):
        if self.db == None:
            client = MongoClient('mongodb://localhost:27017/EventsProject')
            return client.EventProject.TwitterTeste
        else:
            return self.db
        
    def on_status(self, status):
        try:
            #Stop condition
            if (dt.datetime.now().strftime('%H:%M') == self.stop_condition):
                return False
            
            #print(self.status_wrapper.fill(status.text))
            #print('\n %s  %s  via %s\n' % (status.author.screen_name, status.created_at, status.source))
            
            json_tw = status._json
            
            eventDB = self.connect_mongoDB_Twitter()
            result = eventDB.insert_one(json_tw)
            
            
            #print(eventDB.command("serverStatus"))
            #post = {"author": "Mike","text": "My first blog post!","tags": ["mongodb", "python", "pymongo"]}
            #print(result.inserted_id)
            
            text = " "
            if 'text' in json_tw:
                text = json_tw['text']
            full_text = " "
            if 'extended_tweet' in json_tw and 'full_text' in json_tw:
                full_text = json_tw['extended_tweet']['full_text']
            
            user_name = json_tw['user']['name']
            user_screen_name = json_tw['user']['screen_name']
            user_description = json_tw['user']['description']
            
            quoted_status_text = " "
            if 'quoted_status' in json_tw:
                quoted_status = json_tw['quoted_status']['text']
            
            hashtags = json_tw['entities']['hashtags']
            user_cited = json_tw['entities']['user_mentions']
                
            twitter_obj = twitter_object(json_tw['id_str'],json_tw['created_at'], text, full_text, user_name, user_screen_name, user_description, " ", user_cited)
            #print(twitter_obj.formatToStandardString())
              
            kafka_producer = self.connect_kafka_producer()
            self.publish_message(kafka_producer, 'mytopicTwitter', 'twitter', twitter_obj.formatToStandardString())
            print(twitter_obj.formatToStandardString())
                        
            #print("%s" % json.dumps(json_tw, ensure_ascii=False))
#             try:
#                 print("Full: %s" % json_tw['extended_tweet']['full_text'])
#             except:
#                 print("Short: %s" % json_tw['text'])
            print('----------------------------------')
            
#             print(l.__len__())
#             if l.__len__() > 5:
#                 print("AQUIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII")
#                 stop(self)
#                 print(self._flag)
#             print(self._flag)
            return self._flag
        except:
            #print('Exception while sending twitters to kafka.')
            # Catch any unicode errors while printing to console
            # and just ignore them to avoid breaking application.
            pass

    def on_error(self, status_code):
        print('An error has occured! Status code = %s' % status_code)
        return True  # keep stream alive

    def on_timeout(self):
        print('Snoozing Zzzzzz')


def main(stop_hour):
    
    # Prompt for login credentials and setup stream object
    consumer_key = 'jrVA0gkCeKtmPsmFOnyJCC8wz' #input('Consumer Key: ') #jrVA0gkCeKtmPsmFOnyJCC8wz
    consumer_secret = 'maEHcUInH767Ot4fZmmIpWVx8rWTUzD01NaXOQJtn4hYfJtMkv' #getpass('Consumer Secret: ') #maEHcUInH767Ot4fZmmIpWVx8rWTUzD01NaXOQJtn4hYfJtMkv
    access_token = '1154435537765355521-0e1UOx64RigNtsp5SyKTx7it6A2own' #input('Access Token: ') #1154435537765355521-0e1UOx64RigNtsp5SyKTx7it6A2own
    access_token_secret = 'l2OyQdhzLWcxG8pZuYZiSbtnpuxmKRQBuHxqXjq6Yhj4J' #getpass('Access Token Secret: ') #l2OyQdhzLWcxG8pZuYZiSbtnpuxmKRQBuHxqXjq6Yhj4J
    
    auth = tweepy.auth.OAuthHandler(consumer_key, consumer_secret)
    
    
    auth.set_access_token(access_token, access_token_secret)
    listener = StreamWatcherListener()
    listener.stop_condition = stop_hour
    stream = tweepy.Stream(auth, listener, timeout=None)
    #https://boundingbox.klokantech.com/ site to get the cities bounding box
    curitiba_bounding_box = [-73.712054491,-33.5974337638,-34.337054491,6.2921284478] #recife -34.977052,-8.155187,-34.82393,-7.949604 #[-49.381,-25.572,-49.118,-25.318] #brazil:-73.712054491,-33.5974337638,-34.337054491,6.2921284478
    stream.filter(locations=curitiba_bounding_box, languages=["pt"]) #en = english, es = spanish
    
    stream.disconnect()
    
    #return self

if __name__ == '__main__':
    try:
        main('23:35')
    except KeyboardInterrupt:
        print('\nGoodbye!')