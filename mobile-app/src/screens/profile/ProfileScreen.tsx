import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Image,
  Alert,
  Switch,
  ActivityIndicator,
} from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import * as SecureStore from 'expo-secure-store';
import { Colors, Typography, Spacing, BorderRadius, AnimationDuration } from '../../theme/theme';
import { Icons, Skeleton } from '../../components/icons/Icons';

type Props = NativeStackScreenProps<any, 'Profile'>;

interface UserProfile {
  id: string;
  openid?: string;
  nickname?: string;
  avatar?: string;
  phone?: string;
  preferences?: {
    dietaryRestrictions?: string[];
    favoriteFoods?: string[];
    dislikedFoods?: string[];
  };
}

export const ProfileScreen: React.FC<Props> = ({ navigation }) => {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [notificationsEnabled, setNotificationsEnabled] = useState(true);
  const [locationEnabled, setLocationEnabled] = useState(true);

  useEffect(() => {
    loadUserProfile();
  }, []);

  const loadUserProfile = async () => {
    try {
      const userInfo = await SecureStore.getItemAsync('userInfo');
      if (userInfo) {
        setUser(JSON.parse(userInfo));
      }
    } catch (error) {
      console.error('Failed to load user profile:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    Alert.alert(
      '退出登录',
      '确定要退出登录吗？',
      [
        { text: '取消', style: 'cancel' },
        {
          text: '确定',
          style: 'destructive',
          onPress: async () => {
            try {
              await SecureStore.deleteItemAsync('authToken');
              await SecureStore.deleteItemAsync('refreshToken');
              await SecureStore.deleteItemAsync('userInfo');
              navigation.reset({
                index: 0,
                routes: [{ name: 'Login' }],
              });
            } catch (error) {
              Alert.alert('错误', '退出登录失败，请重试');
            }
          },
        },
      ]
    );
  };

  const menuItems = [
    {
      id: 'preferences',
      icon: 'User',
      title: '个人偏好',
      description: '饮食偏好、忌口设置',
      onPress: () => navigation.navigate('Preferences'),
    },
    {
      id: 'history',
      icon: 'History',
      title: '推荐历史',
      description: '查看历史推荐记录',
      onPress: () => navigation.navigate('History'),
    },
    {
      id: 'favorites',
      icon: 'Heart',
      title: '我的收藏',
      description: '收藏的食物和地点',
      onPress: () => navigation.navigate('Favorites'),
    },
    {
      id: 'notifications',
      icon: 'Bell',
      title: '通知设置',
      description: '推送通知管理',
      rightElement: (
        <Switch
          value={notificationsEnabled}
          onValueChange={setNotificationsEnabled}
          trackColor={{ false: '#E5E5E5', true: Colors.primary }}
        />
      ),
    },
    {
      id: 'location',
      icon: 'Location',
      title: '位置服务',
      description: '位置权限和精度设置',
      rightElement: (
        <Switch
          value={locationEnabled}
          onValueChange={setLocationEnabled}
          trackColor={{ false: '#E5E5E5', true: Colors.primary }}
        />
      ),
    },
    {
      id: 'about',
      icon: 'Info',
      title: '关于我们',
      description: '版本信息和联系方式',
      onPress: () => navigation.navigate('About'),
    },
    {
      id: 'feedback',
      icon: 'Edit',
      title: '意见反馈',
      description: '帮助我们改进',
      onPress: () => navigation.navigate('Feedback'),
    },
  ];

  const stats = [
    { id: 'total', label: '推荐次数', value: user?.preferences?.favoriteFoods?.length || 0 },
    { id: 'favorites', label: '收藏数量', value: user?.preferences?.favoriteFoods?.length || 0 },
    { id: 'days', label: '使用天数', value: 7 },
  ];

  if (loading) {
    return (
      <View style={styles.container}>
        <View style={styles.header}>
          <Skeleton width={80} height={80} style={{ borderRadius: 40 }} />
          <View style={{ marginLeft: Spacing.lg }}>
            <Skeleton width={120} height={24} style={{ marginBottom: Spacing.sm }} />
            <Skeleton width={180} height={16} />
          </View>
        </View>
        <View style={styles.statsContainer}>
          {[1, 2, 3].map((i) => (
            <View key={i} style={styles.statItem}>
              <Skeleton width={60} height={30} />
              <Skeleton width={80} height={14} />
            </View>
          ))}
        </View>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container} showsVerticalScrollIndicator={false}>
      {/* Header with user info */}
      <View style={styles.header}>
        <View style={styles.avatarContainer}>
          {user?.avatar ? (
            <Image source={{ uri: user.avatar }} style={styles.avatar} />
          ) : (
            <View style={[styles.avatar, styles.avatarPlaceholder]}>
              <Icons.User size={40} color={Colors.primary} />
            </View>
          )}
          <TouchableOpacity style={styles.editAvatarButton} onPress={() => {}}>
            <Icons.Edit size={16} color="#fff" />
          </TouchableOpacity>
        </View>

        <View style={styles.userInfo}>
          <Text style={styles.userName}>{user?.nickname || 'NovaFlow用户'}</Text>
          <Text style={styles.userPhone}>
            {user?.phone ? user.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '未绑定手机'}
          </Text>
        </View>
      </View>

      {/* Stats */}
      <View style={styles.statsContainer}>
        {stats.map((stat) => (
          <View key={stat.id} style={styles.statItem}>
            <Text style={styles.statValue}>{stat.value}</Text>
            <Text style={styles.statLabel}>{stat.label}</Text>
          </View>
        ))}
      </View>

      {/* VIP Banner */}
      <TouchableOpacity style={styles.vipBanner} onPress={() => {}}>
        <View style={styles.vipContent}>
          <Text style={styles.vipIcon}>👑</Text>
          <View style={styles.vipText}>
            <Text style={styles.vipTitle}>升级VIP会员</Text>
            <Text style={styles.vipDescription}>解锁更多高级功能</Text>
          </View>
        </View>
        <Icons.ArrowRight size={20} color={Colors.warning} />
      </TouchableOpacity>

      {/* Menu Items */}
      <View style={styles.menuSection}>
        <Text style={styles.menuSectionTitle}>账户设置</Text>

        {menuItems.slice(0, 4).map((item) => (
          <TouchableOpacity
            key={item.id}
            style={styles.menuItem}
            onPress={item.onPress}
            activeOpacity={0.7}
          >
            <View style={styles.menuItemLeft}>
              <View style={styles.menuIcon}>
                {item.id === 'preferences' && <Icons.User size={24} color={Colors.primary} />}
                {item.id === 'history' && <Icons.History size={24} color={Colors.primary} />}
                {item.id === 'favorites' && <Icons.Heart size={24} color={Colors.error} />}
                {item.id === 'notifications' && <Icons.Bell size={24} color={Colors.primary} />}
              </View>
              <View style={styles.menuItemText}>
                <Text style={styles.menuItemTitle}>{item.title}</Text>
                <Text style={styles.menuItemDescription}>{item.description}</Text>
              </View>
            </View>
            {item.rightElement || <Icons.ArrowRight size={20} color={Colors.textTertiary} />}
          </TouchableOpacity>
        ))}
      </View>

      <View style={styles.menuSection}>
        <Text style={styles.menuSectionTitle}>更多</Text>

        {menuItems.slice(4).map((item) => (
          <TouchableOpacity
            key={item.id}
            style={styles.menuItem}
            onPress={item.onPress}
            activeOpacity={0.7}
          >
            <View style={styles.menuItemLeft}>
              <View style={styles.menuIcon}>
                {item.id === 'location' && <Icons.Location size={24} color={Colors.primary} />}
                {item.id === 'about' && <Icons.Info size={24} color={Colors.info} />}
                {item.id === 'feedback' && <Icons.Edit size={24} color={Colors.primary} />}
              </View>
              <View style={styles.menuItemText}>
                <Text style={styles.menuItemTitle}>{item.title}</Text>
                <Text style={styles.menuItemDescription}>{item.description}</Text>
              </View>
            </View>
            {item.rightElement || <Icons.ArrowRight size={20} color={Colors.textTertiary} />}
          </TouchableOpacity>
        ))}
      </View>

      {/* Logout Button */}
      <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
        <Text style={styles.logoutButtonText}>退出登录</Text>
      </TouchableOpacity>

      {/* Version Info */}
      <Text style={styles.versionText}>NovaFlow v1.0.0</Text>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.background,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: Spacing.xl,
    backgroundColor: Colors.card,
    borderBottomWidth: 1,
    borderBottomColor: Colors.backgroundDark,
  },
  avatarContainer: {
    position: 'relative',
  },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: 40,
    backgroundColor: Colors.backgroundLight,
  },
  avatarPlaceholder: {
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: Colors.backgroundLight,
  },
  editAvatarButton: {
    position: 'absolute',
    bottom: 0,
    right: 0,
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: Colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 2,
    borderColor: Colors.card,
  },
  userInfo: {
    marginLeft: Spacing.lg,
    flex: 1,
  },
  userName: {
    ...Typography.h3,
    color: Colors.textPrimary,
    marginBottom: Spacing.xs,
  },
  userPhone: {
    ...Typography.bodySmall,
    color: Colors.textTertiary,
  },
  statsContainer: {
    flexDirection: 'row',
    backgroundColor: Colors.card,
    marginHorizontal: Spacing.xl,
    marginTop: Spacing.lg,
    borderRadius: BorderRadius.lg,
    padding: Spacing.lg,
    ...Colors.shadow.small,
  },
  statItem: {
    flex: 1,
    alignItems: 'center',
  },
  statValue: {
    ...Typography.h2,
    color: Colors.primary,
    marginBottom: Spacing.xs,
  },
  statLabel: {
    ...Typography.bodySmall,
    color: Colors.textTertiary,
  },
  vipBanner: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#FFF8E7',
    marginHorizontal: Spacing.xl,
    marginTop: Spacing.lg,
    paddingHorizontal: Spacing.lg,
    paddingVertical: Spacing.md,
    borderRadius: BorderRadius.lg,
    borderWidth: 1,
    borderColor: '#FFE082',
  },
  vipContent: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  vipIcon: {
    fontSize: 32,
    marginRight: Spacing.md,
  },
  vipText: {
    flex: 1,
  },
  vipTitle: {
    ...Typography.emphasis,
    color: '#D48806',
    marginBottom: 2,
  },
  vipDescription: {
    ...Typography.bodyTiny,
    color: '#D48806',
  },
  menuSection: {
    marginTop: Spacing.xl,
    paddingHorizontal: Spacing.xl,
  },
  menuSectionTitle: {
    ...Typography.emphasisSmall,
    color: Colors.textTertiary,
    marginBottom: Spacing.md,
    marginLeft: Spacing.xs,
  },
  menuItem: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: Colors.card,
    paddingHorizontal: Spacing.lg,
    paddingVertical: Spacing.lg,
    borderRadius: BorderRadius.md,
    marginBottom: Spacing.sm,
    ...Colors.shadow.small,
  },
  menuItemLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  menuIcon: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: Colors.backgroundLight,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: Spacing.md,
  },
  menuItemText: {
    flex: 1,
  },
  menuItemTitle: {
    ...Typography.emphasis,
    color: Colors.textPrimary,
    marginBottom: 2,
  },
  menuItemDescription: {
    ...Typography.bodyTiny,
    color: Colors.textTertiary,
  },
  logoutButton: {
    backgroundColor: Colors.card,
    marginHorizontal: Spacing.xl,
    marginTop: Spacing.xl,
    paddingVertical: Spacing.lg,
    borderRadius: BorderRadius.md,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: Colors.error,
  },
  logoutButtonText: {
    ...Typography.emphasis,
    color: Colors.error,
  },
  versionText: {
    ...Typography.bodyTiny,
    color: Colors.textTertiary,
    textAlign: 'center',
    marginTop: Spacing.xl,
    marginBottom: Spacing.xxl,
  },
});
