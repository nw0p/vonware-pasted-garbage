package fat.vonware.manager;

import fat.vonware.features.Feature;
import fat.vonware.features.modules.client.FriendNotify;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FriendManager
        extends Feature {
    private List<Friend> friends = new ArrayList<Friend>();

    public FriendManager() {
        super("Friends");
    }

    public boolean isFriend(String name) {
        this.cleanFriends();
        return this.friends.stream().anyMatch(friend -> friend.username.equalsIgnoreCase(name));
    }

    public boolean isFriend(EntityPlayer player) {
        return this.isFriend(player.getName());
    }

    public boolean isAliasSameAsLabel(final String aliasOrLabel) {
        for (final Friend friend : this.friends) {
            if (aliasOrLabel.equalsIgnoreCase(friend.getUsername()) && aliasOrLabel.equalsIgnoreCase(friend.getUsername())) {
                return true;
            }
        }
        return false;
    }
    public Friend getFriend(final String label) {
        for (final Friend friend : this.friends) {
            if (friend.getUsername().equalsIgnoreCase(label)) {
                return friend;
            }
        }
        return null;
    }

    public void addFriend(String name) {
        Friend friend = this.getFriendByName(name);
        if (friend != null) {
            this.friends.add(friend);
            FriendNotify.INSTANCE.alert(name, false);
        }
        this.cleanFriends();
    }

    public void removeFriend(String name) {
        this.cleanFriends();
        for (Friend friend : this.friends) {
            if (!friend.getUsername().equalsIgnoreCase(name)) continue;
            this.friends.remove(friend);
            FriendNotify.INSTANCE.alert(name, true);
            break;
        }
    }

    public void onLoad() {
        this.friends = new ArrayList<Friend>();
        this.clearSettings();
    }

    public void saveFriends() {
        this.clearSettings();
        this.cleanFriends();
        for (Friend friend : this.friends) {
            this.register(new Setting<String>(friend.getUuid().toString(), friend.getUsername()));
        }
    }

    public void cleanFriends() {
        this.friends.stream().filter(Objects::nonNull).filter(friend -> friend.getUsername() != null);
    }

    public List<Friend> getFriends() {
        this.cleanFriends();
        return this.friends;
    }

    public Friend getFriendByName(String input) {
        UUID uuid = PlayerUtil.getUUIDFromName(input);
        if (uuid != null) {
            Friend friend = new Friend(input, uuid);
            return friend;
        }
        return null;
    }

    public void addFriend(Friend friend) {
        this.friends.add(friend);
    }

    public static class Friend {
        private final String username;
        private final UUID uuid;

        public Friend(String username, UUID uuid) {
            this.username = username;
            this.uuid = uuid;
        }

        public String getUsername() {
            return this.username;
        }

        public UUID getUuid() {
            return this.uuid;
        }
    }
}

