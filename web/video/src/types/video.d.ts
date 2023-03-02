declare namespace Video {
  interface Page {
    current?: number;
    size?: number;
    sortField?: string;
    sortOrder?: string;
    searchKey?: string;
    title?: string;
  }
  interface Info {
    id: string;
    userId: string;
    user?: User.Info;
    title: string;
    m3u8: string;
    poster: string;
    time: string;
    size: number;
    createTime: string;
    status: number;
    viewCount: number;
    grade: number;
  }

  interface TranscodeConfig {
    poster: string; // 截取封面的时间
    tsSeconds: number; // ts分片大小，单位是秒
    cutStart: string; // 视频裁剪，开始时间
    cutEnd: string; // 视频裁剪，结束时间
  }
  interface Update {
    id: string;
    title: string;
    grade: number;
    status: number;
  }
  interface Process {
    videoId: string;
    process: number;
  }
}
