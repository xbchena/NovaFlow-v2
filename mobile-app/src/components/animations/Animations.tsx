/**
 * 通用动画组件
 * 提供常用的动画效果
 */

import React, { useEffect, useRef } from 'react';
import { View, Animated, StyleSheet, Dimensions, Platform } from 'react-native';
import { Colors, AnimationDuration } from '../../theme/theme';

const { width: SCREEN_WIDTH } = Dimensions.get('window');

/**
 * 淡入动画组件
 */
export const FadeIn: React.FC<{
  children: React.ReactNode;
  duration?: number;
  delay?: number;
  style?: any;
}> = ({ children, duration = AnimationDuration.normal, delay = 0, style }) => {
  const fadeAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.timing(fadeAnim, {
      toValue: 1,
      duration,
      delay,
      useNativeDriver: true,
    }).start();
  }, []);

  return (
    <Animated.View style={{ opacity: fadeAnim, ...style }}>
      {children}
    </Animated.View>
  );
};

/**
 * 从下向上滑入动画组件
 */
export const SlideUp: React.FC<{
  children: React.ReactNode;
  duration?: number;
  delay?: number;
  distance?: number;
  style?: any;
}> = ({ children, duration = AnimationDuration.normal, delay = 0, distance = 30, style }) => {
  const slideAnim = useRef(new Animated.Value(distance)).current;

  useEffect(() => {
    Animated.timing(slideAnim, {
      toValue: 0,
      duration,
      delay,
      useNativeDriver: true,
    }).start();
  }, []);

  return (
    <Animated.View style={{ transform: [{ translateY: slideAnim }], ...style }}>
      {children}
    </Animated.View>
  );
};

/**
 * 从左向右滑入动画组件
 */
export const SlideInRight: React.FC<{
  children: React.ReactNode;
  duration?: number;
  delay?: number;
  distance?: number;
  style?: any;
}> = ({ children, duration = AnimationDuration.normal, delay = 0, distance = 50, style }) => {
  const slideAnim = useRef(new Animated.Value(distance)).current;

  useEffect(() => {
    Animated.timing(slideAnim, {
      toValue: 0,
      duration,
      delay,
      useNativeDriver: true,
    }).start();
  }, []);

  return (
    <Animated.View style={{ transform: [{ translateX: slideAnim }], ...style }}>
      {children}
    </Animated.View>
  );
};

/**
 * 缩放动画组件
 */
export const ScaleIn: React.FC<{
  children: React.ReactNode;
  duration?: number;
  delay?: number;
  initialScale?: number;
  style?: any;
}> = ({ children, duration = AnimationDuration.normal, delay = 0, initialScale = 0.9, style }) => {
  const scaleAnim = useRef(new Animated.Value(initialScale)).current;

  useEffect(() => {
    Animated.spring(scaleAnim, {
      toValue: 1,
      tension: 50,
      friction: 7,
      delay,
      useNativeDriver: true,
    }).start();
  }, []);

  return (
    <Animated.View style={{ transform: [{ scale: scaleAnim }], ...style }}>
      {children}
    </Animated.View>
  );
};

/**
 * 列表项交错动画组件
 */
export const StaggerInList: React.FC<{
  children: React.ReactNode;
  index: number;
  delay?: number;
  style?: any;
}> = ({ children, index, delay = 50, style }) => {
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const slideAnim = useRef(new Animated.Value(20)).current;

  useEffect(() => {
    const staggerDelay = index * delay;

    Animated.parallel([
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: AnimationDuration.normal,
        delay: staggerDelay,
        useNativeDriver: true,
      }),
      Animated.timing(slideAnim, {
        toValue: 0,
        duration: AnimationDuration.normal,
        delay: staggerDelay,
        useNativeDriver: true,
      }),
    ]).start();
  }, [index]);

  return (
    <Animated.View
      style={{
        opacity: fadeAnim,
        transform: [{ translateY: slideAnim }],
        ...style,
      }}
    >
      {children}
    </Animated.View>
  );
};

/**
 * 加载动画组件
 */
export const LoadingSpinner: React.FC<{
  size?: number;
  color?: string;
}> = ({ size = 40, color = Colors.primary }) => {
  const rotateAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.loop(
      Animated.timing(rotateAnim, {
        toValue: 1,
        duration: 1000,
        useNativeDriver: true,
      })
    ).start();
  }, []);

  const spin = rotateAnim.interpolate({
    inputRange: [0, 1],
    outputRange: ['0deg', '360deg'],
  });

  return (
    <Animated.View style={{ transform: [{ rotate: spin }] }}>
      <View
        style={{
          width: size,
          height: size,
          borderRadius: size / 2,
          borderTopColor: color,
          borderRightColor: 'transparent',
          borderBottomColor: 'transparent',
          borderLeftColor: color,
          borderWidth: 3,
        }}
      />
    </Animated.View>
  );
};

/**
 * 脉冲动画组件
 */
export const Pulse: React.FC<{
  children: React.ReactNode;
  style?: any;
}> = ({ children, style }) => {
  const scaleAnim = useRef(new Animated.Value(1)).current;

  useEffect(() => {
    Animated.loop(
      Animated.sequence([
        Animated.timing(scaleAnim, {
          toValue: 1.05,
          duration: 150,
          useNativeDriver: true,
        }),
        Animated.timing(scaleAnim, {
          toValue: 1,
          duration: 150,
          useNativeDriver: true,
        }),
      ])
    ).start();
  }, []);

  return (
    <Animated.View style={{ transform: [{ scale: scaleAnim }], ...style }}>
      {children}
    </Animated.View>
  );
};

/**
 * 摇晃动画组件（用于错误提示等）
 */
export const Shake: React.FC<{
  children: React.ReactNode;
  trigger: boolean;
  style?: any;
}> = ({ children, trigger, style }) => {
  const translateAnim = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    if (trigger) {
      Animated.sequence([
        Animated.timing(translateAnim, {
          toValue: -10,
          duration: 50,
          useNativeDriver: true,
        }),
        Animated.timing(translateAnim, {
          toValue: 10,
          duration: 50,
          useNativeDriver: true,
        }),
        Animated.timing(translateAnim, {
          toValue: -10,
          duration: 50,
          useNativeDriver: true,
        }),
        Animated.timing(translateAnim, {
          toValue: 10,
          duration: 50,
          useNativeDriver: true,
        }),
        Animated.timing(translateAnim, {
          toValue: 0,
          duration: 50,
          useNativeDriver: true,
        }),
      ]).start();
    }
  }, [trigger]);

  return (
    <Animated.View style={{ transform: [{ translateX: translateAnim }], ...style }}>
      {children}
    </Animated.View>
  );
};

/**
 * 骨架屏加载动画组件
 */
export const Skeleton: React.FC<{
  width?: number | string;
  height?: number;
  style?: any;
  variant?: 'rect' | 'circle';
}> = ({ width = '100%', height = 20, style, variant = 'rect' }) => {
  const opacityAnim = useRef(new Animated.Value(0.3)).current;

  useEffect(() => {
    Animated.loop(
      Animated.sequence([
        Animated.timing(opacityAnim, {
          toValue: 0.6,
          duration: 800,
          useNativeDriver: true,
        }),
        Animated.timing(opacityAnim, {
          toValue: 0.3,
          duration: 800,
          useNativeDriver: true,
        }),
      ])
    ).start();
  }, []);

  return (
    <Animated.View
      style={[
        {
          width,
          height,
          backgroundColor: '#E5E5E5',
          borderRadius: variant === 'circle' ? (typeof height === 'number' ? height / 2 : 9999) : 4,
          opacity: opacityAnim,
        },
        style,
      ]}
    />
  );
};

/**
 * 页面切换配置
 */
export const screenTransitionConfig = () => ({
  transitionSpec: {
    open: {
      animation: 'timing',
      config: {
        duration: AnimationDuration.normal,
      },
    },
    close: {
      animation: 'timing',
      config: {
        duration: AnimationDuration.fast,
      },
    },
  },
  cardStyleInterpolator: ({ current, next, layouts }: any) => {
    const translateFocused = Animated.multiply(
      current.progress.interpolate({
        inputRange: [0, 1],
        outputRange: [0, 1],
      }),
      layouts.screen.width
    );

    const translateUnfocused = Animated.multiply(
      current.progress.interpolate({
        inputRange: [0, 1],
        outputRange: [0, -0.3],
      }),
      layouts.screen.width
    );

    return {
      cardStyle: {
        transform: [
          {
            translateX: next
              ? translateUnfocused
              : translateFocused,
          },
        ],
      },
    };
  },
});

/**
 * 导出所有动画工具函数
 */
export const AnimationUtils = {
  /**
   * 创建并行动画
   */
  parallel: (animations: Animated.CompositeAnimation[]) => {
    return Animated.parallel(animations);
  },

  /**
   * 创建序列动画
   */
  sequence: (animations: Animated.CompositeAnimation[]) => {
    return Animated.sequence(animations);
  },

  /**
   * 创建延迟动画
   */
  delay: (callback: () => void, ms: number) => {
    return setTimeout(callback, ms);
  },

  /**
   * 创建弹簧动画
   */
  spring: (value: Animated.Value, toValue: number, config?: any) => {
    return Animated.spring(value, {
      toValue,
      tension: 50,
      friction: 7,
      useNativeDriver: true,
      ...config,
    });
  },

  /**
   * 创建缓动动画
   */
  timing: (value: Animated.Value, toValue: number, duration?: number) => {
    return Animated.timing(value, {
      toValue,
      duration: duration || AnimationDuration.normal,
      useNativeDriver: true,
    });
  },
};
