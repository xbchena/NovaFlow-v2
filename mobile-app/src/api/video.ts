import { apiClient } from './client';
import * as FileSystem from 'expo-file-system';

export interface VideoUploadResult {
  success: boolean;
  videoId?: string;
  message?: string;
}

export interface VideoAnalysisResult {
  success: boolean;
  data?: {
    sceneType: string;
    sceneDescription: string;
    recommendations: Array<{
      foodName: string;
      reason: string;
      priceHint?: string;
      matchScore: number;
    }>;
    nearbyPlaces?: Array<{
      id: string;
      name: string;
      address: string;
      distance: number;
      category: string;
    }>;
  };
  message?: string;
}

export class VideoService {
  /**
   * 上传视频
   */
  async uploadVideo(
    videoUri: string,
    duration: number,
    location?: { latitude: number; longitude: number },
    onProgress?: (progress: number) => void
  ): Promise<VideoUploadResult> {
    try {
      // 获取文件信息
      const fileInfo = await FileSystem.getInfoAsync(videoUri);
      if (!fileInfo.exists) {
        return { success: false, message: '文件不存在' };
      }

      // 创建 FormData
      const formData = new FormData();
      formData.append('video', {
        uri: videoUri,
        type: 'video/mp4',
        name: `video_${Date.now()}.mp4`,
      } as any);
      formData.append('duration', duration.toString());

      if (location) {
        formData.append('latitude', location.latitude.toString());
        formData.append('longitude', location.longitude.toString());
      }

      // 上传
      const response = await apiClient.upload('/videos/upload', formData, onProgress);

      return {
        success: response.success,
        videoId: response.data?.videoId,
        message: response.message,
      };
    } catch (error) {
      return {
        success: false,
        message: '视频上传失败',
      };
    }
  }

  /**
   * 获取视频分析结果
   */
  async getAnalysisResult(videoId: string): Promise<VideoAnalysisResult> {
    try {
      const response = await apiClient.get(`/videos/${videoId}/analysis`);
      return response;
    } catch (error) {
      return {
        success: false,
        message: '获取分析结果失败',
      };
    }
  }

  /**
   * 轮询等待分析完成
   */
  async waitForAnalysis(
    videoId: string,
    onProgress?: (status: string) => void
  ): Promise<VideoAnalysisResult> {
    const maxAttempts = 30; // 最多等待30次
    const interval = 2000; // 每2秒检查一次

    for (let i = 0; i < maxAttempts; i++) {
      const result = await this.getAnalysisResult(videoId);

      if (result.success) {
        return result;
      }

      if (result.message?.includes('failed')) {
        return result;
      }

      onProgress?.(`分析中... ${Math.min((i + 1) * 3, 99)}%`);
      await new Promise(resolve => setTimeout(resolve, interval));
    }

    return {
      success: false,
      message: '分析超时',
    };
  }

  /**
   * 获取用户视频列表
   */
  async getUserVideos(page = 1, pageSize = 20) {
    try {
      const response = await apiClient.get('/videos', { page, pageSize });
      return response;
    } catch (error) {
      return { success: false, data: { videos: [], total: 0 } };
    }
  }

  /**
   * 删除视频
   */
  async deleteVideo(videoId: string): Promise<boolean> {
    try {
      await apiClient.delete(`/videos/${videoId}`);
      return true;
    } catch {
      return false;
    }
  }
}

export const videoService = new VideoService();
