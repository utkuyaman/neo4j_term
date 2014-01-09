package tr.edu.dogus.neo4j.db;

/**
 * 
 * @author toshiba
 */
public class TwitterUser {

	private Integer userId;
	private Long twitterUserId;
	private String name;

	public TwitterUser(Integer userId, Long twitterUserId, String name) {
		this.userId = userId;
		this.twitterUserId = twitterUserId;
		this.name = name;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Long getTwitterUserId() {
		return twitterUserId;
	}

	public void setTwitterUserId(Long twitterUserId) {
		this.twitterUserId = twitterUserId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "TwitterUser [userId=" + userId + ", twitterUserId=" + twitterUserId + ", name=" + name + "]";
	}

}
