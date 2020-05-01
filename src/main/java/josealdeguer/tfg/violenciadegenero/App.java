package josealdeguer.tfg.violenciadegenero;

import twitter4j.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class App {
    private static Connect conexion = new Connect();

    public static void main( String[] args )
    {
        Twitter twitter = new TwitterFactory().getInstance();

        try {
            // Query query = new Query("(independencia) OR (independentismo)");
            Query query = new Query("(independencia) OR (independentismo) OR (separatistas)");
            // Query query = new Query("violencia de genero");
            query.count(100);
            QueryResult result;

            do {

                Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
                String endpoint = "/search/tweets";
                RateLimitStatus status = rateLimitStatus.get(endpoint);
                System.out.println(" Endpoint: " + endpoint);
                System.out.println(" Limit: " + status.getLimit());
                System.out.println(" Remaining: " + status.getRemaining());
                System.out.println(" ResetTimeInSeconds: " + status.getResetTimeInSeconds());
                System.out.println(" SecondsUntilReset: " + status.getSecondsUntilReset());

                result = twitter.search(query);
                List<Status> tweets = result.getTweets();

                for(int i= 0; i<tweets.size(); i++){
                    Status tweet = tweets.get(i);
                    if(!tweet.isRetweet()){
                        String sql = "INSERT INTO tweets (tweet_id, text, user, gathered_at, fecha, isRetweet, retweets, favorites, user_location, country, city) " +
                                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                        try{
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date();

                            // tweet.repl
                            PreparedStatement pst = conexion.getPreparedStatement(sql);
                            pst.setLong(1, tweet.getId());
                            pst.setString(2, tweet.getText());
                            pst.setString(3, tweet.getUser().getScreenName());
                            pst.setString(4, dateFormat.format(date));
                            pst.setTimestamp(5, new java.sql.Timestamp(tweet.getCreatedAt().getTime()));
                            pst.setBoolean(6, tweet.getRetweetedStatus() != null);
                            pst.setInt(7, tweet.getRetweetCount());
                            pst.setInt(8, tweet.getFavoriteCount());
                            pst.setString(9, tweet.getUser().getLocation());
                            if (tweet.getPlace() != null) {
                                pst.setString(10, tweet.getPlace().getCountry());
                                pst.setString(11, tweet.getPlace().getName());
                            } else {
                                pst.setString(10, "");
                                pst.setString(11, "");
                            }

                            conexion.addToDB(pst);
                            conexion.closeConnection();
                        } catch(SQLException e){
                            System.out.println("Error sql: "+e.getMessage());
                            e.printStackTrace();
                        } finally {
                            conexion.closeConnection();
                        }

//						System.out.println(i + ".- " + "@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
//						System.out.println(tweet.getCreatedAt().getTime());
//						if(tweet.getPlace() != null){
//							System.out.println("Country: " + tweet.getPlace().getCountry());
//							System.out.println("FullName: " + tweet.getPlace().getFullName());
//							System.out.println("Name: " + tweet.getPlace().getName());
//							System.out.println("Street Address: " + tweet.getPlace().getStreetAddress());
//						}
                    }
                }
                TimeUnit.SECONDS.sleep(5);
            } while ((query = result.nextQuery()) != null);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//		try {
////			Twitter twitter = new TwitterFactory().getInstance();
//			Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
//			//for (String endpoint : rateLimitStatus.keySet()) {
//			String endpoint = "/search/tweets";
//			RateLimitStatus status = rateLimitStatus.get(endpoint);
//			System.out.println("Endpoint: " + endpoint);
//			System.out.println(" Limit: " + status.getLimit());
//			System.out.println(" Remaining: " + status.getRemaining());
//			System.out.println(" ResetTimeInSeconds: " + status.getResetTimeInSeconds());
//			System.out.println(" SecondsUntilReset: " + status.getSecondsUntilReset());
//			//}
//			System.exit(0);
//		} catch (TwitterException te) {
//			te.printStackTrace();
//			System.out.println("Failed to get rate limit status: " + te.getMessage());
//			System.exit(-1);
//		}

		/*
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			Query query = new Query(args[0]);
			QueryResult result;
			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
				}
			} while ((query = result.nextQuery()) != null);
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		}
		*/
    }

    public static Twitter getTwitterinstance() {
        /**
         * if not using properties file, we can set access token by following way
         */
//		ConfigurationBuilder cb = new ConfigurationBuilder();
//		cb.setDebugEnabled(true)
//		  .setOAuthConsumerKey("//TODO")
//		  .setOAuthConsumerSecret("//TODO")
//		  .setOAuthAccessToken("//TODO")
//		  .setOAuthAccessTokenSecret("//TODO");
//		TwitterFactory tf = new TwitterFactory(cb.build());
//		Twitter twitter = tf.getSingleton();

        Twitter twitter = TwitterFactory.getSingleton();
        return twitter;
    }

    public static String createTweet(String tweet) throws TwitterException {
        Twitter twitter = getTwitterinstance();
        Status status = twitter.updateStatus("creating baeldung API");
        return status.getText();
    }

    public static List<String> getTimeLine() throws TwitterException {
        Twitter twitter = getTwitterinstance();
        List<Status> statuses = twitter.getHomeTimeline();
        return statuses.stream().map(
                item -> item.getText()).collect(
                Collectors.toList());
    }

    public static String sendDirectMessage(String recipientName, String msg) throws TwitterException {
        Twitter twitter = getTwitterinstance();
        DirectMessage message = twitter.sendDirectMessage(recipientName, msg);
        return message.getText();
    }

    public static List<String> searchtweets() throws TwitterException {

        Twitter twitter = getTwitterinstance();
        Query query = new Query("source:twitter4j baeldung");
        QueryResult result = twitter.search(query);

        return result.getTweets().stream()
                .map(item -> item.getText())
                .collect(Collectors.toList());
    }

    public static void streamFeed() {

        StatusListener listener = new StatusListener(){

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice arg) {
                System.out.println("Got a status deletion notice id:" + arg.getStatusId());
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onStatus(Status status) {
                System.out.println(status.getUser().getName() + " : " + status.getText());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }
        };

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        twitterStream.addListener(listener);

        twitterStream.sample();
    }

}