import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Image,
  Alert,
  ActivityIndicator,
  RefreshControl,
  ScrollView,
} from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { Colors, Typography, Spacing, BorderRadius, AnimationDuration } from '../../theme/theme';
import { Icons, Skeleton, FoodImagePlaceholder } from '../../components/icons/Icons';

type Props = NativeStackScreenProps<any, 'History'>;

interface HistoryItem {
  id: string;
  sceneType: string;
  sceneDescription: string;
  recommendations: Array<{
    foodName: string;
    foodType: string;
    matchScore: number;
  }>;
  createdAt: string;
  selectedFood?: string;
  feedback?: 'positive' | 'neutral' | 'negative';
}

const FILTER_OPTIONS = [
  { id: 'all', label: '全部' },
  { id: 'week', label: '本周' },
  { id: 'month', label: '本月' },
  { id: 'feedback', label: '已反馈' },
];

export const HistoryScreen: React.FC<Props> = ({ navigation }) => {
  const [history, setHistory] = useState<HistoryItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [selectedFilter, setSelectedFilter] = useState('all');
  const [filteredHistory, setFilteredHistory] = useState<HistoryItem[]>([]);

  useEffect(() => {
    loadHistory();
  }, []);

  useEffect(() => {
    applyFilter(selectedFilter);
  }, [history, selectedFilter]);

  const loadHistory = async () => {
    setLoading(true);
    try {
      // TODO: Replace with actual API call
      // Mock data for now
      const mockHistory: HistoryItem[] = [
        {
          id: '1',
          sceneType: '餐厅',
          sceneDescription: '您在餐厅环境中，正在考虑点餐',
          recommendations: [
            { foodName: '红烧肉', foodType: '中餐', matchScore: 85 },
            { foodName: '清蒸鱼', foodType: '中餐', matchScore: 78 },
          ],
          createdAt: '2024-03-10 12:30',
          selectedFood: '红烧肉',
          feedback: 'positive',
        },
        {
          id: '2',
          sceneType: '街边',
          sceneDescription: '您在街边小吃摊附近',
          recommendations: [
            { foodName: '煎饼果子', foodType: '小吃', matchScore: 92 },
            { foodName: '烤串', foodType: '烧烤', matchScore: 88 },
          ],
          createdAt: '2024-03-09 18:45',
        },
        {
          id: '3',
          sceneType: '办公室',
          sceneDescription: '午餐时间，在办公室附近',
          recommendations: [
            { foodName: '盖浇饭', foodType: '快餐', matchScore: 80 },
          ],
          createdAt: '2024-03-08 12:15',
          selectedFood: '盖浇饭',
          feedback: 'neutral',
        },
      ];

      setHistory(mockHistory);
    } catch (error) {
      Alert.alert('错误', '加载历史记录失败');
    } finally {
      setLoading(false);
    }
  };

  const applyFilter = (filterId: string) => {
    setSelectedFilter(filterId);

    let filtered = [...history];

    switch (filterId) {
      case 'week':
        // Filter for this week
        filtered = filtered.filter(item => {
          const date = new Date(item.createdAt);
          const now = new Date();
          const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
          return date >= weekAgo;
        });
        break;
      case 'month':
        // Filter for this month
        filtered = filtered.filter(item => {
          const date = new Date(item.createdAt);
          const now = new Date();
          const monthAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
          return date >= monthAgo;
        });
        break;
      case 'feedback':
        // Filter for items with feedback
        filtered = filtered.filter(item => item.feedback);
        break;
    }

    setFilteredHistory(filtered);
  };

  const handleRefresh = async () => {
    setRefreshing(true);
    await loadHistory();
    setRefreshing(false);
  };

  const handleDelete = (itemId: string) => {
    Alert.alert(
      '删除记录',
      '确定要删除这条历史记录吗？',
      [
        { text: '取消', style: 'cancel' },
        {
          text: '删除',
          style: 'destructive',
          onPress: () => {
            setHistory(prev => prev.filter(item => item.id !== itemId));
          },
        },
      ]
    );
  };

  const renderHistoryItem = ({ item }: { item: HistoryItem }) => (
    <TouchableOpacity
      style={styles.historyItem}
      onPress={() => navigation.navigate('HistoryDetail', { itemId: item.id })}
      activeOpacity={0.7}
    >
      {/* Left: Date & Scene */}
      <View style={styles.historyItemLeft}>
        <View style={styles.dateContainer}>
          <Text style={styles.dateDay}>
            {new Date(item.createdAt).getDate().toString().padStart(2, '0')}
          </Text>
          <Text style={styles.dateMonth}>
            {new Date(item.createdAt).toLocaleString('zh-CN', { month: 'short' })}
          </Text>
        </View>
      </View>

      {/* Middle: Content */}
      <View style={styles.historyItemContent}>
        <View style={styles.sceneBadge}>
          <Text style={styles.sceneBadgeText}>{item.sceneType}</Text>
        </View>
        <Text style={styles.sceneDescription} numberOfLines={2}>
          {item.sceneDescription}
        </Text>
        <View style={styles.recommendationsPreview}>
          {item.recommendations.slice(0, 3).map((rec, index) => (
            <View key={index} style={styles.recommendationChip}>
              <Text style={styles.recommendationChipText}>{rec.foodName}</Text>
              <View style={[styles.matchScore, { backgroundColor: getMatchColor(rec.matchScore) }]}>
                <Text style={styles.matchScoreText}>{rec.matchScore}%</Text>
              </View>
            </View>
          ))}
          {item.recommendations.length > 3 && (
            <Text style={styles.moreText}>+{item.recommendations.length - 3}</Text>
          )}
        </View>

        {/* Feedback indicator */}
        {item.feedback && (
          <View style={styles.feedbackIndicator}>
            {item.feedback === 'positive' && <Icons.Check size={16} color={Colors.success} />}
            {item.feedback === 'neutral' && <Text style={styles.feedbackNeutral}>○</Text>}
            {item.feedback === 'negative' && <Text style={styles.feedbackNegative}>✕</Text>}
            <Text style={styles.feedbackText}>
              {item.feedback === 'positive' && '满意'}
              {item.feedback === 'neutral' && '一般'}
              {item.feedback === 'negative' && '不满意'}
            </Text>
          </View>
        )}
      </View>

      {/* Right: Action */}
      <TouchableOpacity
        style={styles.deleteButton}
        onPress={() => handleDelete(item.id)}
        hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
      >
        <Icons.Trash size={18} color={Colors.textTertiary} />
      </TouchableOpacity>
    </TouchableOpacity>
  );

  const getMatchColor = (score: number) => {
    if (score >= 80) return Colors.success;
    if (score >= 60) return Colors.warning;
    return Colors.error;
  };

  const renderEmptyState = () => (
    <View style={styles.emptyState}>
      <Icons.History size={64} color={Colors.textTertiary} />
      <Text style={styles.emptyStateTitle}>暂无历史记录</Text>
      <Text style={styles.emptyStateDescription}>开始拍摄视频，获取您的第一个推荐吧</Text>
      <TouchableOpacity
        style={styles.emptyStateButton}
        onPress={() => navigation.navigate('Camera')}
      >
        <Text style={styles.emptyStateButtonText}>开始使用</Text>
      </TouchableOpacity>
    </View>
  );

  const renderHeader = () => (
    <View style={styles.header}>
      <Text style={styles.title}>推荐历史</Text>
      <Text style={styles.subtitle}>查看您的历史推荐记录</Text>

      {/* Filter Tabs */}
      <View style={styles.filterContainer}>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.filterScroll}>
          {FILTER_OPTIONS.map((filter) => (
            <TouchableOpacity
              key={filter.id}
              style={[
                styles.filterChip,
                selectedFilter === filter.id && styles.filterChipActive,
              ]}
              onPress={() => applyFilter(filter.id)}
              activeOpacity={0.7}
            >
              <Text
                style={[
                  styles.filterChipText,
                  selectedFilter === filter.id && styles.filterChipTextActive,
                ]}
              >
                {filter.label}
              </Text>
            </TouchableOpacity>
          ))}
        </ScrollView>
      </View>
    </View>
  );

  if (loading) {
    return (
      <View style={styles.container}>
        {renderHeader()}
        <View style={styles.skeletonContainer}>
          {[1, 2, 3].map((i) => (
            <View key={i} style={styles.historyItem}>
              <Skeleton width={50} height={50} style={{ borderRadius: 8 }} />
              <View style={{ flex: 1, marginLeft: Spacing.md }}>
                <Skeleton width={100} height={16} style={{ marginBottom: Spacing.sm }} />
                <Skeleton width="80%" height={14} style={{ marginBottom: Spacing.sm }} />
                <Skeleton width={120} height={12} />
              </View>
            </View>
          ))}
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <FlatList
        data={filteredHistory}
        renderItem={renderHistoryItem}
        keyExtractor={(item) => item.id}
        ListHeaderComponent={renderHeader}
        ListEmptyComponent={renderEmptyState}
        contentContainerStyle={filteredHistory.length === 0 ? styles.emptyList : undefined}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={handleRefresh} colors={[Colors.primary]} />
        }
        showsVerticalScrollIndicator={false}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.background,
  },
  header: {
    padding: Spacing.xl,
    backgroundColor: Colors.card,
  },
  title: {
    ...Typography.h2,
    color: Colors.textPrimary,
    marginBottom: Spacing.xs,
  },
  subtitle: {
    ...Typography.bodySmall,
    color: Colors.textTertiary,
    marginBottom: Spacing.lg,
  },
  filterContainer: {
    marginTop: Spacing.md,
  },
  filterScroll: {
    paddingHorizontal: -Spacing.md,
  },
  filterChip: {
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.sm,
    backgroundColor: Colors.backgroundLight,
    borderRadius: BorderRadius.full,
    marginRight: Spacing.sm,
  },
  filterChipActive: {
    backgroundColor: Colors.primary,
  },
  filterChipText: {
    ...Typography.bodySmall,
    color: Colors.textSecondary,
  },
  filterChipTextActive: {
    color: Colors.card,
    fontWeight: '600',
  },
  historyItem: {
    flexDirection: 'row',
    backgroundColor: Colors.card,
    marginHorizontal: Spacing.xl,
    marginTop: Spacing.md,
    padding: Spacing.lg,
    borderRadius: BorderRadius.lg,
    ...Colors.shadow.small,
  },
  historyItemLeft: {
    marginRight: Spacing.md,
  },
  dateContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    width: 50,
    height: 50,
    borderRadius: BorderRadius.md,
    backgroundColor: Colors.backgroundLight,
  },
  dateDay: {
    ...Typography.h3,
    color: Colors.primary,
    lineHeight: 24,
  },
  dateMonth: {
    ...Typography.bodyTiny,
    color: Colors.textTertiary,
  },
  historyItemContent: {
    flex: 1,
  },
  sceneBadge: {
    alignSelf: 'flex-start',
    backgroundColor: Colors.primaryLight + '20',
    paddingHorizontal: Spacing.sm,
    paddingVertical: 4,
    borderRadius: BorderRadius.sm,
    marginBottom: Spacing.sm,
  },
  sceneBadgeText: {
    ...Typography.bodyTiny,
    color: Colors.primary,
    fontWeight: '600',
  },
  sceneDescription: {
    ...Typography.bodySmall,
    color: Colors.textPrimary,
    marginBottom: Spacing.sm,
  },
  recommendationsPreview: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginTop: Spacing.sm,
  },
  recommendationChip: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: Colors.backgroundLight,
    paddingHorizontal: Spacing.sm,
    paddingVertical: 4,
    borderRadius: BorderRadius.sm,
    marginRight: Spacing.xs,
    marginBottom: Spacing.xs,
  },
  recommendationChipText: {
    ...Typography.bodyTiny,
    color: Colors.textSecondary,
    marginRight: 4,
  },
  matchScore: {
    paddingHorizontal: 6,
    paddingVertical: 2,
    borderRadius: 8,
  },
  matchScoreText: {
    ...Typography.bodyTiny,
    color: Colors.card,
    fontWeight: '600',
  },
  moreText: {
    ...Typography.bodyTiny,
    color: Colors.textTertiary,
    marginLeft: Spacing.xs,
  },
  feedbackIndicator: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: Spacing.sm,
  },
  feedbackNeutral: {
    fontSize: 16,
    color: Colors.warning,
    marginRight: 4,
  },
  feedbackNegative: {
    fontSize: 16,
    color: Colors.error,
    marginRight: 4,
  },
  feedbackText: {
    ...Typography.bodyTiny,
    color: Colors.textTertiary,
    marginLeft: 4,
  },
  deleteButton: {
    padding: Spacing.sm,
    marginLeft: Spacing.sm,
  },
  emptyList: {
    flex: 1,
  },
  emptyState: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: Spacing.xxl,
  },
  emptyStateTitle: {
    ...Typography.h3,
    color: Colors.textPrimary,
    marginTop: Spacing.lg,
    marginBottom: Spacing.sm,
  },
  emptyStateDescription: {
    ...Typography.bodySmall,
    color: Colors.textTertiary,
    textAlign: 'center',
    marginBottom: Spacing.xl,
  },
  emptyStateButton: {
    backgroundColor: Colors.primary,
    paddingHorizontal: Spacing.xxl,
    paddingVertical: Spacing.md,
    borderRadius: BorderRadius.full,
  },
  emptyStateButtonText: {
    ...Typography.emphasis,
    color: Colors.card,
  },
  skeletonContainer: {
    padding: Spacing.xl,
  },
});
