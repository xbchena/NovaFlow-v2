import { apiClient } from './client';
import { ApiResult } from './types';
import * as Location from 'expo-location';

export interface NearbyPlace {
  id: string;
  name: string;
  address: string;
  distance: number;
  category: 'market' | 'restaurant' | 'cafeteria';
  rating?: number;
  phone?: string;
  location: {
    latitude: number;
    longitude: number;
  };
}

export class LocationService {
  /**
   * 获取当前位置
   */
  async getCurrentPosition(): Promise<{ latitude: number; longitude: number } | null> {
    try {
      const { status } = await Location.requestForegroundPermissionsAsync();

      if (status !== 'granted') {
        return null;
      }

      const location = await Location.getCurrentPositionAsync({
        accuracy: Location.Accuracy.Balanced,
      });

      return {
        latitude: location.coords.latitude,
        longitude: location.coords.longitude,
      };
    } catch {
      return null;
    }
  }

  /**
   * 搜索附近POI
   */
  async searchNearby(params: {
    latitude: number;
    longitude: number;
    radius?: number;
    category?: 'market' | 'restaurant' | 'cafeteria';
  }): Promise<{ success: boolean; data?: NearbyPlace[] }> {
    try {
      const response = await apiClient.get<NearbyPlace[]>('/locations/nearby', params);
      return {
        success: response.success,
        data: response.data,
      };
    } catch {
      return { success: false };
    }
  }

  /**
   * 获取附近市场
   */
  async getNearbyMarkets(latitude: number, longitude: number) {
    return this.searchNearby({ latitude, longitude, category: 'market' });
  }

  /**
   * 获取附近餐厅
   */
  async getNearbyRestaurants(latitude: number, longitude: number) {
    return this.searchNearby({ latitude, longitude, category: 'restaurant' });
  }

  /**
   * 获取附近食堂
   */
  async getNearbyCafeterias(latitude: number, longitude: number) {
    return this.searchNearby({ latitude, longitude, category: 'cafeteria' });
  }

  /**
   * 计算距离
   */
  calculateDistance(
    lat1: number,
    lon1: number,
    lat2: number,
    lon2: number
  ): number {
    const R = 6371e3;
    const φ1 = (lat1 * Math.PI) / 180;
    const φ2 = (lat2 * Math.PI) / 180;
    const Δφ = ((lat2 - lat1) * Math.PI) / 180;
    const Δλ = ((lon2 - lon1) * Math.PI) / 180;

    const a =
      Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
      Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  }
}

export const locationService = new LocationService();
