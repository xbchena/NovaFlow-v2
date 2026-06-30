import axios, { AxiosError, AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Constants from 'expo-constants';

import { ApiResult, normalizeApiResult } from './types';

const API_BASE_URL =
  Constants.expoConfig?.extra?.apiBaseUrl ?? 'https://api.novaflow.com/api/v1';

class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    this.client.interceptors.request.use(
      async (config: InternalAxiosRequestConfig) => {
        const token = await AsyncStorage.getItem('authToken');
        if (token && config.headers) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    this.client.interceptors.response.use(
      (response) => response,
      async (error: AxiosError) => {
        if (error.response?.status === 401) {
          await AsyncStorage.multiRemove(['authToken', 'refreshToken', 'userInfo']);
        }
        return Promise.reject(error);
      }
    );
  }

  private async wrap<T>(promise: Promise<{ data: unknown }>): Promise<ApiResult<T>> {
    try {
      const response = await promise;
      return normalizeApiResult<T>(response.data);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        return normalizeApiResult<T>(error.response?.data ?? { message: error.message });
      }
      return { success: false, message: '网络请求失败' };
    }
  }

  public get = <T = unknown>(url: string, params?: Record<string, unknown>): Promise<ApiResult<T>> =>
    this.wrap<T>(this.client.get(url, { params }));

  public post = <T = unknown>(url: string, data?: unknown): Promise<ApiResult<T>> =>
    this.wrap<T>(this.client.post(url, data));

  public put = <T = unknown>(url: string, data?: unknown): Promise<ApiResult<T>> =>
    this.wrap<T>(this.client.put(url, data));

  public delete = <T = unknown>(url: string): Promise<ApiResult<T>> =>
    this.wrap<T>(this.client.delete(url));

  public patch = <T = unknown>(url: string, data?: unknown): Promise<ApiResult<T>> =>
    this.wrap<T>(this.client.patch(url, data));

  public upload = <T = unknown>(
    url: string,
    formData: FormData,
    onProgress?: (progress: number) => void
  ): Promise<ApiResult<T>> =>
    this.wrap<T>(
      this.client.post(url, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        onUploadProgress: (progressEvent) => {
          if (onProgress && progressEvent.total) {
            const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
            onProgress(progress);
          }
        },
      })
    );
}

export const apiClient = new ApiClient();
