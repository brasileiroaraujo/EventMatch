import schedule
import time
import twitter_kafka as tk
import threading
import datetime as dt
from itertools import count


iid = count()
list_to_stop = ['23:40']
# Functions setup 
def call_twitter():
    tk.main(list_to_stop[iid.__next__()])

def thread_get_twitter(): 
    t = threading.Thread(target=call_twitter())
    #t = multiprocessing.Process(target=call_twitter)
    t.start()



print("Starting ...")
schedule.every().wednesday.at('21:42').do(thread_get_twitter)
# schedule.every().saturday.at('17:45').do(thread_get_twitter)
# schedule.every().saturday.at('19:12').do(thread_get_twitter)
# schedule.every().saturday.at('21:06').do(thread_get_twitter)

# schedule.every().sunday.at('11:06').do(thread_get_twitter)
# schedule.every().sunday.at('16:33').do(thread_get_twitter)
# schedule.every().sunday.at('19:06').do(thread_get_twitter)



# schedule.every().thursday.at('22:53').do(thread_get_twitter)
# schedule.every().thursday.at("21:52").do(thread_get_twitter('21:53'))
#schedule.every().thursday.at("21:38").do(thread_get_twitter("21:39"))
# schedule.every().thursday.at("20:48").do(stop_twitter)
# schedule.every().thursday.at("19:02").do(thread_stop_twitter)
# schedule.every().thursday.at("18:21").do(changeFlag)


while True:
    schedule.run_pending()
    time.sleep(10)
  
# Loop so that the scheduling task 
# keeps on running all time. 
# while True: 
#   
#     # Checks whether a scheduled task  
#     # is pending to run or not 
#     schedule.run_pending() 
#     time.sleep(1)
