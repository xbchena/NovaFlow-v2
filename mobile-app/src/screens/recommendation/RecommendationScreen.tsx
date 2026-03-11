import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  Image,
  Dimensions,
  Animated,
} from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { recommendationService } from '../../api';
import { Colors, Typography, Spacing, BorderRadius, AnimationDuration } from '../../../theme/theme';
import { Icons, FoodImagePlaceholder } from '../../../components/icons/Icons';

type Props = NativeStackScreenProps<any, 'Recommendation'>;

const { width } = Dimensions.get('window');
const CARD_WIDTH = (width - Spacing.xl * 2 - Spacing.md) / 2;

export const RecommendationScreen: React.FC<Props> = ({ route, navigation }) => {
  const { videoId, analysisData } = route.params;
  const [selectedCategory, setSelectedCategory] = useState<'food' | 'places'>('food');
  const [favoriteFoods, setFavoriteFoods] = useState<Set<string>>(new Set());
  const [expandedCards, setExpandedCards] = useState<Set<string>>(new Set());
  const fadeAnim = React.useRef(new Animated.Value(0)).current;
  const slideUpAnim = React.useRef(new Animated.Value(30)).current;

  useEffect(() => {
    Animated.parallel([
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: AnimationDuration.normal,
        useNativeDriver: true,
      }),
      Animated.timing(slideUpAnim, {
        toValue: 0,
        duration: AnimationDuration.normal,
        useNativeDriver: true,
      }),
    ]).start();
  }, []);

  const toggleFavorite = (foodName: string) => {
    setFavoriteFoods(prev => {
      const newFavorites = new Set(prev);
      if (newFavorites.has(foodName)) {
        newFavorites.delete(foodName);
      } else {
        newFavorites.add(foodName);
      }
      return newFavorites;
    });
  };

  const toggleCard = (foodName: string) => {
    setExpandedCards(prev => {
      const newExpanded = new Set(prev);
      if (newExpanded.has(foodName)) {
        newExpanded.delete(foodName);
      } else {
        newExpanded.add(foodName);
      }
      return newExpanded;
    });
  };

  const handleSelectFood = async (foodName: string) => {
    try {
      await recommendationService.recordSelection({
        videoId,
        selectedFood: foodName,
        feedback: 'positive',
      });

      Alert.alert('已记录', `您选择了: ${foodName}`, [
        {
          text: '查看附近',
          onPress: () => setSelectedCategory('places'),
        },
        {
          text: '返回首页',
          onPress: () => navigation.navigate('Home'),
        },
      ]);
    } catch {
      Alert.alert('错误', '记录失败，请重试');
    }
  };

  const handleSelectPlace = (place: any) => {
    Alert.alert(place.name, `${place.address}\n距离: ${place.distance}米`, [
      {
        text: '导航',
        onPress: () => {
          // TODO: Open in maps app
        },
      },
      {
        text: '收藏',
        onPress: () => {
          Alert.alert('已收藏', place.name);
        },
      },
      {
        text: '关闭',
        style: 'cancel',
      },
    ]);
  };

  const handleShare = async () => {
    try {
      // TODO: Implement share functionality
      Alert.alert('分享', '分享功能即将上线');
    } catch (error) {
      Alert.alert('错误', '分享失败');
    }
  };

  const getMatchColor = (score: number) => {
    if (score >= 80) return Colors.success;
    if (score >= 60) return Colors.warning;
    return Colors.error;
  };

  const renderFoodCard = (item: any, index: number) => {
    const isFavorite = favoriteFoods.has(item.foodName);
    const isExpanded = expandedCards.has(item.foodName);

    return (
      <Animated.View
        key={index}
        style={[
          styles.recommendationCard,
          {
            opacity: fadeAnim,
            transform: [{ translateY: slideUpAnim }],
          },
        ]}
      >
        <TouchableOpacity
          onPress={() => toggleCard(item.foodName)}
          activeOpacity={0.9}
        >
          {/* Food Image or Placeholder */}
          {item.imageUrl ? (
            <Image source={{ uri: item.imageUrl }} style={styles.foodImage} />
          ) : (
            <View style={styles.foodImagePlaceholder}>
              <FoodImagePlaceholder size={50} />
              <Text style={styles.foodImagePlaceholderText}>{item.foodName.charAt(0)}</Text>
            </View>
          )}

          {/* Match Score Badge */}
          <View style={[styles.matchScoreBadge, { backgroundColor: getMatchColor(item.matchScore) }]}>
            <Text style={styles.matchScoreBadgeText}>{item.matchScore}%</Text>
          </View>

          {/* Favorite Button */}
          <TouchableOpacity
            style={styles.favoriteButton}
            onPress={() => toggleFavorite(item.foodName)}
            hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
          >
            <Icons.Heart size={18} color={isFavorite ? Colors.error : '#fff'} filled={isFavorite} />
          </TouchableOpacity>
        </TouchableOpacity>

        {/* Content */}
        <View style={styles.foodCardContent}>
          <View style={styles.recommendationHeader}>
            <Text style={styles.foodName}>{item.foodName}</Text>
            <View style={styles.foodTypeBadge}>
              <Text style={styles.foodTypeBadgeText}>{item.foodType}</Text>
            </View>
          </View>

          <Text style={styles.foodReason}>{item.reason}</Text>

          {/* Expanded Content */}
          {isExpanded && (
            <View style={styles.expandedContent}>
              {/* Nutrition Info */}
              {item.nutrition && (
                <View style={styles.nutritionInfo}>
                  <Text style={styles.nutritionTitle}>营养成分（每100g）</Text>
                  <View style={styles.nutritionGrid}>
                    <View style={styles.nutritionItem}>
                      <Text style={styles.nutritionValue}>{item.nutrition.protein || '-'}</Text>
                      <Text style={styles.nutritionLabel}>蛋白质</Text>
                    </View>
                    <View style={styles.nutritionItem}>
                      <Text style={styles.nutritionValue}>{item.nutrition.carbs || '-'}</Text>
                      <Text style={styles.nutritionLabel}>碳水</Text>
                    </View>
                    <View style={styles.nutritionItem}>
                      <Text style={styles.nutritionValue}>{item.nutrition.fat || '-'}</Text>
                      <Text style={styles.nutritionLabel}>脂肪</Text>
                    </View>
                  </View>
                </View>
              )}

              {/* Calories */}
              {item.calories && (
                <View style={styles.caloriesInfo}>
                  <Text style={styles.caloriesIcon}>🔥</Text>
                  <Text style={styles.caloriesText}>{item.calories} 千卡</Text>
                </View>
              )}

              {/* Price */}
              {item.priceHint && (
                <View style={styles.priceInfo}>
                  <Text style={styles.priceIcon}>💰</Text>
                  <Text style={styles.priceText}>{item.priceHint}</Text>
                </View>
              )}
            </View>
          )}

          {/* Select Button */}
          <TouchableOpacity
            style={styles.selectButton}
            onPress={() => handleSelectFood(item.foodName)}
            activeOpacity={0.8}
          >
            <Icons.Check size={16} color="#fff" />
            <Text style={styles.selectButtonText}>选择这个</Text>
          </TouchableOpacity>
        </View>
      </Animated.View>
    );
  };

  const renderPlaceCard = (place: any) => (
    <TouchableOpacity
      key={place.id}
      style={styles.placeCard}
      onPress={() => handleSelectPlace(place)}
      activeOpacity={0.7}
    >
      {place.imageUrl ? (
        <Image source={{ uri: place.imageUrl }} style={styles.placeImage} />
      ) : (
        <View style={styles.placeImagePlaceholder}>
          <Icons.Location size={32} color={Colors.textTertiary} />
        </View>
      )}

      <View style={styles.placeContent}>
        <View style={styles.placeHeader}>
          <Text style={styles.placeName}>{place.name}</Text>
          <View style={styles.placeDistanceBadge}>
            <Text style={styles.placeDistanceText}>{place.distance}m</Text>
          </View>
        </View>

        <Text style={styles.placeAddress} numberOfLines={1}>{place.address}</Text>

        <View style={styles.placeFooter}>
          <View style={styles.placeCategoryBadge}>
            <Text style={styles.placeCategoryText}>
              {place.category === 'market' ? '菜市场' :
               place.category === 'restaurant' ? '餐厅' : '食堂'}
            </Text>
          </View>

          {place.rating && (
            <View style={styles.ratingContainer}>
              <Icons.Star size={14} color={Colors.warning} filled />
              <Text style={styles.ratingText}>{place.rating}</Text>
            </View>
          )}
        </View>
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity
          style={styles.headerButton}
          onPress={() => navigation.goBack()}
        >
          <Icons.ArrowRight size={24} color={Colors.textPrimary} style={{ transform: [{ rotate: '180deg' }] }} />
        </TouchableOpacity>

        <Text style={styles.headerTitle}>推荐结果</Text>

        <TouchableOpacity style={styles.headerButton} onPress={handleShare}>
          <Icons.Share size={24} color={Colors.textPrimary} />
        </TouchableOpacity>
      </View>

      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Scene Analysis */}
        {analysisData.sceneType && (
          <View style={styles.sceneCard}>
            <View style={styles.sceneHeader}>
              <View style={styles.sceneTitleContainer}>
                <Icons.Location size={20} color={Colors.primary} />
                <Text style={styles.sceneTitle}>场景识别</Text>
              </View>
              <View style={styles.sceneBadge}>
                <Text style={styles.sceneBadgeText}>{analysisData.sceneType}</Text>
              </View>
            </View>
            <Text style={styles.sceneDescription}>{analysisData.sceneDescription}</Text>
          </View>
        )}

        {/* Category Tabs */}
        <View style={styles.tabsContainer}>
          <View style={styles.tabs}>
            <TouchableOpacity
              style={[styles.tab, selectedCategory === 'food' && styles.tabActive]}
              onPress={() => setSelectedCategory('food')}
              activeOpacity={0.7}
            >
              <Icons.Camera size={18} color={selectedCategory === 'food' ? '#fff' : Colors.textSecondary} />
              <Text style={[styles.tabText, selectedCategory === 'food' && styles.tabTextActive]}>
                美食推荐
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.tab, selectedCategory === 'places' && styles.tabActive]}
              onPress={() => setSelectedCategory('places')}
              activeOpacity={0.7}
            >
              <Icons.Location size={18} color={selectedCategory === 'places' ? '#fff' : Colors.textSecondary} />
              <Text style={[styles.tabText, selectedCategory === 'places' && styles.tabTextActive]}>
                附近地点
              </Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* Food Recommendations */}
        {selectedCategory === 'food' && analysisData.recommendations && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>为您推荐</Text>
            <View style={styles.foodGrid}>
              {analysisData.recommendations.map((item: any, index: number) => renderFoodCard(item, index))}
            </View>
          </View>
        )}

        {/* Nearby Places */}
        {selectedCategory === 'places' && analysisData.nearbyPlaces && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>附近地点</Text>
            {analysisData.nearbyPlaces.map((place: any) => renderPlaceCard(place))}
          </View>
        )}

        {/* Action Buttons */}
        <View style={styles.actions}>
          <TouchableOpacity
            style={styles.secondaryButton}
            onPress={() => navigation.navigate('Camera')}
          >
            <Icons.Refresh size={20} color={Colors.primary} />
            <Text style={styles.secondaryButtonText}>重新录制</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.primaryButton}
            onPress={() => navigation.navigate('Home')}
          >
            <Icons.Home size={20} color="#fff" />
            <Text style={styles.primaryButtonText}>返回首页</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </View>
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
    justifyContent: 'space-between',
    paddingHorizontal: Spacing.lg,
    paddingVertical: Spacing.md,
    backgroundColor: Colors.card,
    borderBottomWidth: 1,
    borderBottomColor: Colors.backgroundDark,
  },
  headerButton: {
    width: 40,
    height: 40,
    justifyContent: 'center',
    alignItems: 'center',
  },
  headerTitle: {
    ...Typography.h4,
    color: Colors.textPrimary,
  },
  sceneCard: {
    backgroundColor: Colors.card,
    margin: Spacing.lg,
    padding: Spacing.lg,
    borderRadius: BorderRadius.lg,
    ...Colors.shadow.medium,
  },
  sceneHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: Spacing.md,
  },
  sceneTitleContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  sceneTitle: {
    ...Typography.h4,
    color: Colors.textPrimary,
    marginLeft: Spacing.sm,
  },
  sceneBadge: {
    backgroundColor: Colors.primary + '20',
    paddingHorizontal: Spacing.md,
    paddingVertical: Spacing.sm,
    borderRadius: BorderRadius.sm,
  },
  sceneBadgeText: {
    ...Typography.bodySmall,
    color: Colors.primary,
    fontWeight: '600',
  },
  sceneDescription: {
    ...Typography.body,
    color: Colors.textPrimary,
    lineHeight: 22,
  },
  tabsContainer: {
    paddingHorizontal: Spacing.lg,
    paddingTop: Spacing.md,
  },
  tabs: {
    flexDirection: 'row',
    backgroundColor: Colors.backgroundLight,
    borderRadius: BorderRadius.full,
    padding: 4,
  },
  tab: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: Spacing.md,
    borderRadius: BorderRadius.full,
    gap: Spacing.xs,
  },
  tabActive: {
    backgroundColor: Colors.primary,
  },
  tabText: {
    ...Typography.emphasisSmall,
    color: Colors.textSecondary,
  },
  tabTextActive: {
    color: Colors.card,
  },
  section: {
    paddingHorizontal: Spacing.lg,
    paddingTop: Spacing.lg,
  },
  sectionTitle: {
    ...Typography.h3,
    color: Colors.textPrimary,
    marginBottom: Spacing.md,
  },
  foodGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginHorizontal: -Spacing.sm,
  },
  recommendationCard: {
    width: CARD_WIDTH,
    backgroundColor: Colors.card,
    borderRadius: BorderRadius.lg,
    margin: Spacing.sm,
    marginBottom: Spacing.lg,
    overflow: 'hidden',
    ...Colors.shadow.medium,
  },
  foodImage: {
    width: '100%',
    height: CARD_WIDTH * 0.75,
    backgroundColor: Colors.backgroundLight,
  },
  foodImagePlaceholder: {
    width: '100%',
    height: CARD_WIDTH * 0.75,
    backgroundColor: Colors.backgroundLight,
    justifyContent: 'center',
    alignItems: 'center',
    padding: Spacing.lg,
  },
  foodImagePlaceholderText: {
    ...Typography.h2,
    color: Colors.textTertiary,
    marginTop: Spacing.sm,
  },
  matchScoreBadge: {
    position: 'absolute',
    top: Spacing.sm,
    left: Spacing.sm,
    paddingHorizontal: Spacing.sm,
    paddingVertical: Spacing.xs,
    borderRadius: BorderRadius.sm,
    alignItems: 'center',
  },
  matchScoreBadgeText: {
    ...Typography.emphasisSmall,
    color: Colors.card,
    fontSize: 12,
  },
  favoriteButton: {
    position: 'absolute',
    top: Spacing.sm,
    right: Spacing.sm,
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  foodCardContent: {
    padding: Spacing.md,
  },
  recommendationHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: Spacing.sm,
  },
  foodName: {
    ...Typography.emphasis,
    color: Colors.textPrimary,
    flex: 1,
    marginRight: Spacing.xs,
  },
  foodTypeBadge: {
    backgroundColor: Colors.primary + '15',
    paddingHorizontal: Spacing.sm,
    paddingVertical: 2,
    borderRadius: BorderRadius.sm,
  },
  foodTypeBadgeText: {
    ...Typography.bodyTiny,
    color: Colors.primary,
  },
  foodReason: {
    ...Typography.bodySmall,
    color: Colors.textSecondary,
    lineHeight: 18,
    marginBottom: Spacing.sm,
  },
  expandedContent: {
    marginTop: Spacing.sm,
    paddingTop: Spacing.sm,
    borderTopWidth: 1,
    borderTopColor: Colors.backgroundDark,
  },
  nutritionInfo: {
    backgroundColor: Colors.backgroundLight,
    padding: Spacing.sm,
    borderRadius: BorderRadius.sm,
    marginBottom: Spacing.sm,
  },
  nutritionTitle: {
    ...Typography.bodyTiny,
    color: Colors.textTertiary,
    marginBottom: Spacing.xs,
  },
  nutritionGrid: {
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  nutritionItem: {
    alignItems: 'center',
  },
  nutritionValue: {
    ...Typography.emphasisSmall,
    color: Colors.textPrimary,
  },
  nutritionLabel: {
    ...Typography.bodyTiny,
    color: Colors.textTertiary,
    marginTop: 2,
  },
  caloriesInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: Spacing.sm,
  },
  caloriesIcon: {
    fontSize: 16,
    marginRight: Spacing.xs,
  },
  caloriesText: {
    ...Typography.bodySmall,
    color: Colors.textPrimary,
  },
  priceInfo: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: Spacing.sm,
  },
  priceIcon: {
    fontSize: 16,
    marginRight: Spacing.xs,
  },
  priceText: {
    ...Typography.bodySmall,
    color: Colors.textSecondary,
  },
  selectButton: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: Colors.primary,
    paddingVertical: Spacing.sm,
    borderRadius: BorderRadius.md,
    gap: Spacing.xs,
  },
  selectButtonText: {
    ...Typography.emphasisSmall,
    color: Colors.card,
  },
  placeCard: {
    flexDirection: 'row',
    backgroundColor: Colors.card,
    borderRadius: BorderRadius.lg,
    marginBottom: Spacing.md,
    overflow: 'hidden',
    ...Colors.shadow.small,
  },
  placeImage: {
    width: 100,
    height: 100,
    backgroundColor: Colors.backgroundLight,
  },
  placeImagePlaceholder: {
    width: 100,
    height: 100,
    backgroundColor: Colors.backgroundLight,
    justifyContent: 'center',
    alignItems: 'center',
  },
  placeContent: {
    flex: 1,
    padding: Spacing.md,
  },
  placeHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: Spacing.xs,
  },
  placeName: {
    ...Typography.emphasis,
    color: Colors.textPrimary,
    flex: 1,
    marginRight: Spacing.xs,
  },
  placeDistanceBadge: {
    backgroundColor: Colors.primary + '15',
    paddingHorizontal: Spacing.sm,
    paddingVertical: 2,
    borderRadius: BorderRadius.sm,
  },
  placeDistanceText: {
    ...Typography.bodyTiny,
    color: Colors.primary,
    fontWeight: '600',
  },
  placeAddress: {
    ...Typography.bodySmall,
    color: Colors.textTertiary,
    marginBottom: Spacing.sm,
  },
  placeFooter: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  placeCategoryBadge: {
    backgroundColor: Colors.backgroundLight,
    paddingHorizontal: Spacing.sm,
    paddingVertical: 2,
    borderRadius: BorderRadius.sm,
  },
  placeCategoryText: {
    ...Typography.bodyTiny,
    color: Colors.textSecondary,
  },
  ratingContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  ratingText: {
    ...Typography.bodySmall,
    color: Colors.textPrimary,
    fontWeight: '600',
  },
  actions: {
    flexDirection: 'row',
    padding: Spacing.lg,
    gap: Spacing.md,
  },
  primaryButton: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: Colors.primary,
    paddingVertical: Spacing.md,
    borderRadius: BorderRadius.md,
    gap: Spacing.sm,
  },
  primaryButtonText: {
    ...Typography.emphasis,
    color: Colors.card,
  },
  secondaryButton: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: Colors.card,
    paddingVertical: Spacing.md,
    borderRadius: BorderRadius.md,
    borderWidth: 1,
    borderColor: Colors.primary,
    gap: Spacing.sm,
  },
  secondaryButtonText: {
    ...Typography.emphasis,
    color: Colors.primary,
  },
});
