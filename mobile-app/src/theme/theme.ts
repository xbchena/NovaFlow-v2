/**
 * NovaFlow 主题配置
 * 统一的颜色、字体、间距等设计规范
 */

export const Colors = {
  // 主色调
  primary: '#007AFF',
  primaryLight: '#5AC8FA',
  primaryDark: '#0051D5',

  // 辅助色
  secondary: '#34C759',
  secondaryLight: '#30D158',
  secondaryDark: '#248A3D',

  // 背景色
  background: '#F5F5F5',
  backgroundLight: '#FAFAFA',
  backgroundDark: '#E5E5E5',

  // 卡片色
  card: '#FFFFFF',
  cardShadow: 'rgba(0, 0, 0, 0.08)',

  // 文字色
  text: '#000000',
  textPrimary: '#1A1A1A',
  textSecondary: '#666666',
  textTertiary: '#999999',
  textLight: '#CCCCCC',

  // 状态色
  success: '#34C759',
  warning: '#FF9500',
  error: '#FF3B30',
  info: '#5AC8FA',

  // 特殊色
  wechat: '#07C160',
  phone: '#007AFF',
  recording: '#FF3B30',

  // 阴影
  shadow: {
    small: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 1 },
      shadowOpacity: 0.1,
      shadowRadius: 2,
      elevation: 2,
    },
    medium: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 2 },
      shadowOpacity: 0.15,
      shadowRadius: 4,
      elevation: 3,
    },
    large: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 4 },
      shadowOpacity: 0.2,
      shadowRadius: 8,
      elevation: 5,
    },
  },
};

export const Spacing = {
  xs: 4,
  sm: 8,
  md: 12,
  lg: 16,
  xl: 20,
  xxl: 24,
  xxxl: 32,
};

export const Typography = {
  // 标题
  h1: {
    fontSize: 32,
    fontWeight: 'bold' as const,
    lineHeight: 40,
  },
  h2: {
    fontSize: 24,
    fontWeight: 'bold' as const,
    lineHeight: 32,
  },
  h3: {
    fontSize: 20,
    fontWeight: '600' as const,
    lineHeight: 28,
  },
  h4: {
    fontSize: 18,
    fontWeight: '600' as const,
    lineHeight: 24,
  },

  // 正文
  body: {
    fontSize: 16,
    fontWeight: 'normal' as const,
    lineHeight: 24,
  },
  bodySmall: {
    fontSize: 14,
    fontWeight: 'normal' as const,
    lineHeight: 20,
  },
  bodyTiny: {
    fontSize: 12,
    fontWeight: 'normal' as const,
    lineHeight: 16,
  },

  // 强调
  emphasis: {
    fontSize: 16,
    fontWeight: '600' as const,
    lineHeight: 24,
  },
  emphasisSmall: {
    fontSize: 14,
    fontWeight: '600' as const,
    lineHeight: 20,
  },
};

export const BorderRadius = {
  sm: 8,
  md: 12,
  lg: 16,
  xl: 20,
  full: 9999,
};

export const AnimationDuration = {
  fast: 200,
  normal: 300,
  slow: 500,
};
