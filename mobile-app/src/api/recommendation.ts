import { apiClient } from './client';

export interface Recommendation {
  id: string;
  foodName: string;
  foodType: string;
  reason: string;
  priceHint?: string;
  matchScore: number;
  mainIngredients?: string[];
}

export class RecommendationService {
  /**
   * 获取推荐历史
   */
  async getHistory(page = 1, pageSize = 20) {
    try {
      const response = await apiClient.get('/recommendations/history', { page, pageSize });
      return response;
    } catch (error) {
      return { success: false, data: { recommendations: [], total: 0 } };
    }
  }

  /**
   * 记录用户选择
   */
  async recordSelection(data: {
    videoId: string;
    selectedFood: string;
    feedback?: 'positive' | 'neutral' | 'negative';
  }) {
    try {
      const response = await apiClient.post('/recommendations/select', data);
      return response;
    } catch (error) {
      return { success: false, message: '记录失败' };
    }
  }

  /**
   * 获取推荐详情
   */
  async getDetail(id: string) {
    try {
      const response = await apiClient.get(`/recommendations/${id}`);
      return response;
    } catch (error) {
      return { success: false, message: '获取失败' };
    }
  }
}

export const recommendationService = new RecommendationService();
