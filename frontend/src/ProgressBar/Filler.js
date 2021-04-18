import React, { Component } from 'react';
import styles from "./ProgressBar.module.css"

class Filler extends React.Component {
    constructor(props){
        super(props);
    }

    render() {
        const width = this.props.percentage;
        return (
            <div className={styles.filler} style={{width : `${width}%`}}/>
        );
    }
}

export default Filler;
