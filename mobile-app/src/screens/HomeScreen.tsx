import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Image,
} from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';

type Props = NativeStackScreenProps<any, 'Home'>;

export const HomeScreen: React.FC<Props> = ({ navigation }) => {
  const quickActions = [
    {
      id: 'record',
      icon: '🎬',
      title: '录制视频',
      description: '拍摄场景获取推荐',
      onPress: () => navigation.navigate('Camera'),
    },
    {
      id: 'nearby',
      icon: '📍',
      title: '附近美食',
      description: '查找周边餐厅和市场',
      onPress: () => navigation.navigate('Nearby'),
    },
    {
      id: 'history',
      icon: '📋',
      title: '历史记录',
      description: '查看推荐历史',
      onPress: () => navigation.navigate('History'),
    },
    {
      id: 'profile',
      icon: '⚙️',
      title: '个人设置',
      description: '管理您的偏好',
      onPress: () => navigation.navigate('Profile'),
    },
  ];

  return (
    <ScrollView style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <Text style={styles.greeting}>今天吃什么？</Text>
        <Text style={styles.subtitle}>让AI来帮你做决定</Text>
      </View>

      {/* Main Action Button */}
      <TouchableOpacity
        style={styles.mainButton}
        onPress={() => navigation.navigate('Camera')}
      >
        <View style={styles.mainButtonContent}>
          <Text style={styles.mainButtonIcon}>🎬</Text>
          <View style={styles.mainButtonText}>
            <Text style={styles.mainButtonTitle}>录制视频获取推荐</Text>
            <Text style={styles.mainButtonDesc}>
              拍摄您身边的场景，AI智能推荐美食
            </Text>
          </View>
        </View>
      </TouchableOpacity>

      {/* Quick Actions */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>快捷操作</Text>
        <View style={styles.quickActions}>
          {quickActions.map((action) => (
            <TouchableOpacity
              key={action.id}
              style={styles.quickActionCard}
              onPress={action.onPress}
            >
              <Text style={styles.quickActionIcon}>{action.icon}</Text>
              <Text style={styles.quickActionTitle}>{action.title}</Text>
              <Text style={styles.quickActionDesc}>{action.description}</Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5F5F5',
  },
  header: {
    padding: 20,
    backgroundColor: '#fff',
  },
  greeting: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#000',
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
    marginTop: 5,
  },
  mainButton: {
    margin: 20,
    backgroundColor: '#007AFF',
    borderRadius: 16,
    padding: 20,
    shadowColor: '#007AFF',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 8,
  },
  mainButtonContent: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  mainButtonIcon: {
    fontSize: 40,
    marginRight: 15,
  },
  mainButtonText: {
    flex: 1,
  },
  mainButtonTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 5,
  },
  mainButtonDesc: {
    fontSize: 14,
    color: 'rgba(255, 255, 255, 0.8)',
  },
  section: {
    marginTop: 10,
    paddingHorizontal: 20,
    paddingBottom: 20,
  },
  sectionTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#000',
    marginBottom: 15,
  },
  quickActions: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginHorizontal: -10,
  },
  quickActionCard: {
    width: '48%',
    marginHorizontal: '1%',
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 15,
    marginBottom: 10,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  quickActionIcon: {
    fontSize: 32,
    marginBottom: 10,
  },
  quickActionTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#000',
    marginBottom: 5,
  },
  quickActionDesc: {
    fontSize: 12,
    color: '#666',
    textAlign: 'center',
  },
});
